package diskCacheV111.util ;

/**
 * Thrown when group qota exceeded
 */
public class GroupQuotaCacheException extends CacheException {

    private static final long serialVersionUID = 1L;

     public GroupQuotaCacheException( String msg ){
        super(GROUP_QUOTA_EXCEEDED, msg);
     }

    public GroupQuotaCacheException(String msg, Throwable cause) {
        super(GROUP_QUOTA_EXCEEDED, msg, cause);
    }
}
