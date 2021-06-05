package diskCacheV111.util ;

/**
 * Thrown when user qota exceeded
 */
public class UserQuotaCacheException extends CacheException {

    private static final long serialVersionUID = 1L;

     public UserQuotaCacheException( String msg ){
        super(USER_QUOTA_EXCEEDED, msg);
     }

    public UserQuotaCacheException(String msg, Throwable cause) {
        super(USER_QUOTA_EXCEEDED, msg, cause);
    }
}
