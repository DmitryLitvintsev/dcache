package org.dcache.chimera.quota;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskCacheV111.util.RetentionPolicy;
import org.dcache.chimera.ChimeraFsException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;


public class JdbcQuota {
    /**
     * SQL query engine
     */

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JdbcQuota.class);

    private final QuotaSqlDriver sqlDriver;
    private volatile Map<Integer, Quota> userQuotas;
    private volatile Map<Integer, Quota> groupQuotas;

   
    public JdbcQuota(DataSource ds)
            throws SQLException, ChimeraFsException
    {
        sqlDriver = new QuotaSqlDriver(ds);
        userQuotas = sqlDriver.getUserQuotas();
        groupQuotas = sqlDriver.getGroupQuotas();
    }

    public boolean checkUserQuota(int uid, RetentionPolicy rp) {
        Quota quota = userQuotas.get(uid);
        if (quota == null)  {
            LOGGER.info("Failed to find user quota for {}", uid);
            return true;
        } else {
            LOGGER.info("Found group quota for {} {} {}", quota.getId(), quota.getUsedCustodialSpaceLimit(), quota.getUsedReplicaSpaceLimit());
            return quota.check(rp);
        }
    }

    public boolean checkGroupQuota(int gid, RetentionPolicy rp) { 
        Quota quota = groupQuotas.get(gid);
        if (quota == null)  {
            LOGGER.info("Failed to find group quota for {}", gid);
            return true;
        } else {
            LOGGER.info("Found group quota for {} {} {}", quota.getId(), quota.getUsedCustodialSpaceLimit(), quota.getUsedReplicaSpaceLimit());
            return quota.check(rp);
        }
    }

    public void refreshUserQuotas() {
        Map<Integer, Quota> tmp = sqlDriver.getUserQuotas();
        synchronized (this) {
            userQuotas = tmp;
        }
    }

    public void refreshGroupQuotas() {
        Map<Integer, Quota> tmp = sqlDriver.getGroupQuotas();
        synchronized (this) {
            groupQuotas = tmp;
        }
    }

    public void updateUserQuotas() {
        sqlDriver.updateUserQuota();
    }

    public void updateGroupQuotas() {
        sqlDriver.updateGroupQuota();
    }
}
