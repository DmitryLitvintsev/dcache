package org.dcache.chimera.quota;

import diskCacheV111.util.RetentionPolicy;

public class Quota { 
    int id;
    private final long usedCustodialSpace;
    private final long usedReplicaSpace;
    private final long usedOutputSpace;

    private final Long usedCustodialSpaceLimit;
    private final Long usedReplicaSpaceLimit;
    private final Long usedOutputSpaceLimit;

    public Quota(int id,
                 long usedCustodialSpace,
                 Long usedCustodialSpaceLimit,
                 long usedOutputSpace,
                 Long usedOutputSpaceLimit,
                 long usedReplicaSpace,
                 Long usedReplicaSpaceLimit
                 ) {
        this.id = id;
        this.usedCustodialSpace = usedCustodialSpace;
        this.usedReplicaSpace = usedReplicaSpace;
        this.usedOutputSpace = usedOutputSpace;
        this.usedCustodialSpaceLimit = usedCustodialSpaceLimit;
        this.usedOutputSpaceLimit = usedOutputSpaceLimit;
        this.usedReplicaSpaceLimit = usedReplicaSpaceLimit;
    }
    public int getId() {
        return id;
    }
    
    public long getUsedCustodialSpace() {
        return usedCustodialSpace;
    }

    public long getUsedReplicaSpace() {
        return usedReplicaSpace;
    }

    public long getUsedOutputSpace() {
        return usedOutputSpace;
    }

    public Long getUsedCustodialSpaceLimit() {
        return usedCustodialSpaceLimit;
    }

    public Long getUsedReplicaSpaceLimit() {
        return usedReplicaSpaceLimit;
    }

    public Long getUsedOutputSpaceLimit() {
        return usedOutputSpaceLimit;
    }

    public boolean check(RetentionPolicy retentionPolicy) {
        if (retentionPolicy == RetentionPolicy.CUSTODIAL &&
                usedCustodialSpaceLimit != null &&
                usedCustodialSpaceLimit < usedCustodialSpace) {
	        return false;
	    }
        if (retentionPolicy == RetentionPolicy.REPLICA &&
                usedReplicaSpaceLimit != null &&
                usedReplicaSpaceLimit < usedReplicaSpace) {
	        return false;
	    }
	    if (retentionPolicy == RetentionPolicy.OUTPUT &&
                usedOutputSpaceLimit != null &&
                usedOutputSpaceLimit < usedOutputSpace) {
            return false;
        }
	    return true;
    }    
}
