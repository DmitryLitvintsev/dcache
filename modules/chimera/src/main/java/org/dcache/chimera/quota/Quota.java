package org.dcache.chimera.quota;

import diskCacheV111.util.RetentionPolicy;


public class Quota {
    int id;
    private final long usedCustodialSpace;
    private final long usedReplicaSpace;
    private final long usedOutputSpace;

    private Long custodialSpaceLimit;
    private Long replicaSpaceLimit;
    private Long outputSpaceLimit;

    public Quota(int id,
                 long usedCustodialSpace,
                 Long custodialSpaceLimit,
                 long usedOutputSpace,
                 Long outputSpaceLimit,
                 long usedReplicaSpace,
                 Long replicaSpaceLimit) {
        this.id = id;
        this.usedCustodialSpace = usedCustodialSpace;
        this.usedReplicaSpace = usedReplicaSpace;
        this.usedOutputSpace = usedOutputSpace;
        this.custodialSpaceLimit = custodialSpaceLimit;
        this.outputSpaceLimit = outputSpaceLimit;
        this.replicaSpaceLimit = replicaSpaceLimit;
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

    public Long getCustodialSpaceLimit() {
        return custodialSpaceLimit;
    }

    public Long getReplicaSpaceLimit() {
        return replicaSpaceLimit;
    }

    public Long getOutputSpaceLimit() {
        return outputSpaceLimit;
    }

    public void setCustodialSpaceLimit(Long custodialSpaceLimit) {
        this.custodialSpaceLimit = custodialSpaceLimit;
    }

    public void setReplicaSpaceLimit(Long replicaSpaceLimit) {
        this.replicaSpaceLimit = replicaSpaceLimit;
    }

    public void setOutputSpaceLimit(Long outputSpaceLimit) {
        this.outputSpaceLimit = outputSpaceLimit;
    }

    public boolean check(RetentionPolicy retentionPolicy) {
        if (retentionPolicy == RetentionPolicy.CUSTODIAL &&
                custodialSpaceLimit != null &&
                custodialSpaceLimit < usedCustodialSpace) {
            return false;
        }
        if (retentionPolicy == RetentionPolicy.REPLICA &&
                replicaSpaceLimit != null &&
                replicaSpaceLimit < usedReplicaSpace) {
            return false;
        }
        if (retentionPolicy == RetentionPolicy.OUTPUT &&
                outputSpaceLimit != null &&
                outputSpaceLimit < usedOutputSpace) {
            return false;
        }
        return true;
    }
}
