package org.dcache.gplazma.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.InternetDomainName;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.dcache.auth.BearerTokenCredential;
import org.dcache.auth.EmailAddressPrincipal;
import org.dcache.auth.EntitlementPrincipal;
import org.dcache.auth.FullNamePrincipal;
import org.dcache.auth.GroupNamePrincipal;
import org.dcache.auth.LoA;
import org.dcache.auth.LoAPrincipal;
import org.dcache.auth.OAuthProviderPrincipal;
import org.dcache.auth.OidcSubjectPrincipal;
import org.dcache.auth.OpenIdGroupPrincipal;
import org.dcache.auth.UserNamePrincipal;
import org.dcache.gplazma.AuthenticationException;
import org.dcache.gplazma.oidc.helpers.JsonHttpClient;
import org.dcache.gplazma.oidc.userinfo.QueryUserInfoEndpoint;
import org.dcache.gplazma.plugins.GPlazmaAuthenticationPlugin;
import org.dcache.gplazma.util.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static org.dcache.gplazma.oidc.PropertiesUtils.asInt;
import static org.dcache.gplazma.util.Preconditions.checkAuthentication;

public class OidcAuthPlugin implements GPlazmaAuthenticationPlugin {

    private final static Logger LOG = LoggerFactory.getLogger(OidcAuthPlugin.class);

    private final static String HTTP_CONCURRENT_ACCESS = "gplazma.oidc.http.total-concurrent-requests";
    private final static String HTTP_PER_ROUTE_CONCURRENT_ACCESS = "gplazma.oidc.http.per-route-concurrent-requests";
    private final static String HTTP_TIMEOUT = "gplazma.oidc.http.timeout";
    private final static String HTTP_TIMEOUT_UNIT = "gplazma.oidc.http.timeout.unit";
    private final static String OIDC_HOSTNAMES = "gplazma.oidc.hostnames";
    private final static String OIDC_PROVIDER_PREFIX = "gplazma.oidc.provider!";


    /**
     * A mapping from "eduperson_assurance" claim to the corresponding LoA. The details are
     * available in
     * <a href="https://docs.google.com/document/d/1b-Mlet3Lq7qKLEf1BnHJ4nL1fq-vMe7fzpXyrq2wp08/edit">REFEDs
     * OIDCre</a> and in various AARC policies,
     * <a href="https://aarc-project.eu/guidelines/aarc-g021/">AARC-G021</a> and
     * <a href="https://aarc-project.eu/guidelines/aarc-g041/">AARC-G041</a> in
     * particular.
     */
    private static final Map<String, LoA> EDUPERSON_ASSURANCE = ImmutableMap.<String, LoA>builder()
          // REFEDS RAF policies
          .put("https://refeds.org/assurance/ID/unique", LoA.REFEDS_ID_UNIQUE)
          .put("https://refeds.org/assurance/ID/eppn-unique-no-reassign",
                LoA.REFEDS_ID_EPPN_UNIQUE_NO_REASSIGN)
          .put("https://refeds.org/assurance/ID/eppn-unique-reassign-1y",
                LoA.REFEDS_ID_EPPN_UNIQUE_REASSIGN_1Y)
          .put("https://refeds.org/assurance/IAP/low", LoA.REFEDS_IAP_LOW)
          .put("https://refeds.org/assurance/IAP/medium", LoA.REFEDS_IAP_MEDIUM)
          .put("https://refeds.org/assurance/IAP/high", LoA.REFEDS_IAP_HIGH)
          .put("https://refeds.org/assurance/IAP/local-enterprise", LoA.REFEDS_IAP_LOCAL_ENTERPRISE)
          .put("https://refeds.org/assurance/ATP/ePA-1m", LoA.REFEDS_ATP_1M)
          .put("https://refeds.org/assurance/ATP/ePA-1d", LoA.REFEDS_ATP_1D)
          .put("https://refeds.org/assurance/profile/cappuccino", LoA.REFEDS_PROFILE_CAPPUCCINO)
          .put("https://refeds.org/assurance/profile/espresso", LoA.REFEDS_PROFILE_ESPRESSO)

          // IGTF policies  see https://www.igtf.net/ap/authn-assurance/
          .put("https://igtf.net/ap/authn-assurance/aspen", LoA.IGTF_LOA_ASPEN)
          .put("https://igtf.net/ap/authn-assurance/birch", LoA.IGTF_LOA_BIRCH)
          .put("https://igtf.net/ap/authn-assurance/cedar", LoA.IGTF_LOA_CEDAR)
          .put("https://igtf.net/ap/authn-assurance/dogwood", LoA.IGTF_LOA_DOGWOOD)

          // AARC policies see https://aarc-project.eu/guidelines/#policy
          .put("https://aarc-project.eu/policy/authn-assurance/assam", LoA.AARC_PROFILE_ASSAM)

          // EGI policies see https://wiki.egi.eu/wiki/AAI_guide_for_SPs#Level_of_Assurance
          .put("https://aai.egi.eu/LoA#Low", LoA.EGI_LOW)
          .put("https://aai.egi.eu/LoA#Substantial", LoA.EGI_SUBSTANTIAL)
          .put("https://aai.egi.eu/LoA#High", LoA.EGI_HIGH)
          .build();

    private final TokenProcessor tokenProcessor;

    public OidcAuthPlugin(Properties properties) {
        this(properties, buildClientFromProperties(properties));
    }

    @Override
    public void stop() {
        tokenProcessor.shutdown();
    }

    @VisibleForTesting
    OidcAuthPlugin(Properties properties, JsonHttpClient client) {
        tokenProcessor = buildProcessor(properties, client);
    }

    private static TokenProcessor buildProcessor(Properties properties, JsonHttpClient client) {
        Set<IdentityProvider> providers = new HashSet<>();
        providers.addAll(buildHosts(properties));
        providers.addAll(buildProviders(properties));
        checkArgument(!providers.isEmpty(), "No OIDC providers configured");

        return new QueryUserInfoEndpoint(properties, client, providers);
    }

    private static JsonHttpClient buildClientFromProperties(Properties properties) {
        int soTimeout = (int) TimeUnit.valueOf(properties.getProperty(HTTP_TIMEOUT_UNIT))
              .toMillis(asInt(properties, HTTP_TIMEOUT));

        return new JsonHttpClient(asInt(properties, HTTP_CONCURRENT_ACCESS),
              asInt(properties, HTTP_PER_ROUTE_CONCURRENT_ACCESS),
              soTimeout);
    }

    private static Set<IdentityProvider> buildHosts(Properties properties) {
        String oidcHostnamesProperty = properties.getProperty(OIDC_HOSTNAMES);
        checkArgument(oidcHostnamesProperty != null, OIDC_HOSTNAMES + " not defined");

        Map<Boolean, Set<String>> validHosts = Arrays.stream(oidcHostnamesProperty.split("\\s+"))
              .filter(not(String::isEmpty))
              .collect(
                    Collectors.groupingBy(InternetDomainName::isValid,
                          Collectors.toSet())
              );

        Set<String> badHosts = validHosts.get(Boolean.FALSE);
        checkArgument(badHosts == null, "Invalid hosts in %s: %s",
              OIDC_HOSTNAMES, Joiner.on(", ").join(nullToEmpty(badHosts)));

        Set<String> goodHosts = validHosts.get(Boolean.TRUE);
        return goodHosts == null
              ? Collections.emptySet()
              : goodHosts.stream()
                    .map(h -> new IdentityProvider(h, "https://" + h + "/"))
                    .collect(Collectors.toSet());
    }

    private static Set<IdentityProvider> buildProviders(Properties properties) {
        return properties.stringPropertyNames().stream()
              .filter(n -> n.startsWith(OIDC_PROVIDER_PREFIX))
              .map(n -> {
                  try {
                      return new IdentityProvider(n.substring(OIDC_PROVIDER_PREFIX.length()),
                            properties.getProperty(n));
                  } catch (IllegalArgumentException e) {
                      throw new IllegalArgumentException(
                            "Bad OIDC provider " + n + ": " + e.getMessage());
                  }
              })
              .collect(Collectors.toSet());
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    @Override
    public void authenticate(Set<Object> publicCredentials,
          Set<Object> privateCredentials,
          Set<Principal> identifiedPrincipals)
          throws AuthenticationException {

        String token = null;
        for (Object credential : privateCredentials) {
            if (credential instanceof BearerTokenCredential) {
                checkAuthentication(token == null, "Multiple bearer tokens");

                token = ((BearerTokenCredential) credential).getToken();
                LOG.debug("Found bearer token: {}", token);
            }
        }

        checkAuthentication(token != null, "No bearer token in the credentials");
        checkValid(token);

        try {
            ExtractResult result = tokenProcessor.extract(token);

            checkAuthentication(!result.claims().isEmpty(), "processing token yielded no claims");

            var principalsFromToken = principalsFromClaims(result.idp(), result.claims());
            identifiedPrincipals.addAll(principalsFromToken);
        } catch (UnableToProcess e) {
            throw new AuthenticationException("Unable to process token: " + e.getMessage());
        }
    }

    private static void checkValid(String token) throws AuthenticationException {
        if (JsonWebToken.isCompatibleFormat(token)) {
            try {
                JsonWebToken jwt = new JsonWebToken(token);

                Instant now = Instant.now();

                Optional<Instant> exp = jwt.getPayloadInstant("exp");
                checkAuthentication(!exp.isPresent() || now.isBefore(exp.get()),
                      "expired");

                Optional<Instant> nbf = jwt.getPayloadInstant("nbf");
                checkAuthentication(!nbf.isPresent() || now.isAfter(nbf.get()),
                      "not yet valid");
            } catch (IOException e) {
                LOG.debug("Failed to parse token: {}", e.toString());
            }
        }
    }


    private Set<Principal> principalsFromClaims(IdentityProvider ip, Map<String,JsonNode> claims) {
        Set<Principal> principals = new HashSet<>();
        principals.add(new OAuthProviderPrincipal(ip.getName()));
        addSub(ip, claims, principals);
        addNames(claims, principals);
        addEmail(claims, principals);
        Function<String, Principal> toGroupPrincipal = ip.areGroupsAccepted()
              ? OidcAuthPlugin::toGroupName
              : OpenIdGroupPrincipal::new;
        addGroups(claims, principals, toGroupPrincipal);
        addWlcgGroups(claims, principals);
        addLoAs(claims, principals);
        addEntitlements(claims, principals);
        if (ip.isUsernameAccepted()) {
            addUsername(claims, principals);
        }
        return principals;
    }

    private static GroupNamePrincipal toGroupName(String id) {
        /**
         * REVISIT: The group id (as supplied by the OP) may be hierarchical;
         * e.g. "/foo/bar".  For top-level groups (e.g., "/foo") the mapping
         * that removes the initial slash seems reasonable ("/foo" --> "foo");
         * however, how should this be handled more generally?
         * Mapping "/foo/bar" --> foo_bar is one option.  Should this be
         * configurable?
         */
        String name = id.startsWith("/") ? id.substring(1) : id;
        return new GroupNamePrincipal(name);
    }

    private void addEmail(Map<String,JsonNode> claims, Set<Principal> principals) {
        if (claims.containsKey("email")) {
            principals.add(new EmailAddressPrincipal(claims.get("email").asText()));
        }
    }

    private void addNames(Map<String,JsonNode> claims, Set<Principal> principals) {
        JsonNode givenName = claims.get("given_name");
        JsonNode familyName = claims.get("family_name");
        JsonNode fullName = claims.get("name");

        if (fullName != null && !fullName.asText().isEmpty()) {
            principals.add(new FullNamePrincipal(fullName.asText()));
        } else if (givenName != null && !givenName.asText().isEmpty()
              && familyName != null && !familyName.asText().isEmpty()) {
            principals.add(new FullNamePrincipal(givenName.asText(), familyName.asText()));
        }
    }

    private boolean addSub(IdentityProvider ip, Map<String,JsonNode> claims,
          Set<Principal> principals) {
        String claimValue = claims.get("sub").asText();
        return principals.add(new OidcSubjectPrincipal(claimValue, ip.getName()));
    }

    private void addGroups(Map<String,JsonNode> claims, Set<Principal> principals,
          Function<String, Principal> toPrincipal) {
        if (claims.containsKey("groups") && claims.get("groups").isArray()) {
            for (JsonNode group : claims.get("groups")) {
                principals.add(toPrincipal.apply(group.asText()));
            }
        }
    }

    /**
     * Parse group-membership information, as described in "WLCG Common JWT Profiles" v1.0.  For
     * details, see: https://zenodo.org/record/3460258#.YVGMLyXRaV4
     * <p>
     * Here is an example:
     * <pre>
     * "wlcg.groups": [
     *     "/dteam/VO-Admin",
     *     "/dteam",
     *     "/dteam/itcms"
     * ],
     * </pre>
     *
     * @param userInfo   The JSON node describing the user.
     * @param principals The set of principals into which any group information is to be added.
     */
    private void addWlcgGroups(Map<String,JsonNode> claims, Set<Principal> principals) {
        if (!claims.containsKey("wlcg.groups")) {
            return;
        }

        JsonNode groups = claims.get("wlcg.groups");
        if (!groups.isArray()) {
            LOG.debug("Ignoring malformed \"wlcg.groups\": not an array");
            return;
        }

        for (JsonNode group : groups) {
            if (!group.isTextual()) {
                LOG.debug("Ignoring malformed \"wlcg.groups\" value: {}", group);
                continue;
            }
            var groupName = group.asText();
            var principal = new OpenIdGroupPrincipal(groupName);
            principals.add(principal);
        }
    }

    private void addLoAs(Map<String,JsonNode> claims, Set<Principal> principals) {
        if (claims.containsKey("eduperson_assurance") && claims.get("eduperson_assurance").isArray()) {
            StreamSupport.stream(claims.get("eduperson_assurance").spliterator(), false)
                  .map(JsonNode::asText)
                  .map(EDUPERSON_ASSURANCE::get)
                  .filter(Objects::nonNull)
                  // FIXME we need to know when to accept REFEDS_IAP_LOCAL_ENTERPRISE.
                  .filter(l -> l != LoA.REFEDS_IAP_LOCAL_ENTERPRISE)
                  .map(LoAPrincipal::new)
                  .forEach(principals::add);
        }
    }

    /**
     * Add Entitlement principals from mapped eduPersonEntitlement SAML assertions. For details of
     * mapping between SAML assertions and OIDC claims see https://wiki.refeds.org/display/CON/Consultation%3A+SAML2+and+OIDC+Mappings
     */
    private void addEntitlements(Map<String,JsonNode> claims, Set<Principal> principals) {
        JsonNode value = claims.get("eduperson_entitlement");
        if (value == null) {
            return;
        }

        if (value.isArray()) {
            StreamSupport.stream(value.spliterator(), false)
                  .map(JsonNode::asText)
                  .forEach(v -> addEntitlement(principals, v));
        } else if (value.isTextual()) {
            addEntitlement(principals, value.asText());
        }
    }

    private void addUsername(Map<String,JsonNode> claims, Set<Principal> principals) {
        JsonNode value = claims.get("preferred_username");
        if (value != null && value.isTextual()) {
            principals.add(new UserNamePrincipal(value.asText()));
        }
    }


    private void addEntitlement(Set<Principal> principals, String value) {
        try {
            principals.add(new EntitlementPrincipal(value));
        } catch (URISyntaxException e) {
            LOG.debug("Rejecting bad eduperson_entitlement value \"{}\": {}",
                  value, e.getMessage());
        }
    }

    private static <T> Collection<T> nullToEmpty(final Collection<T> collection) {
        return collection == null ? Collections.emptySet() : collection;
    }
}
