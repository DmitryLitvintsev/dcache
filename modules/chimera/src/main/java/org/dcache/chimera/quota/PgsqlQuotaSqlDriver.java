package org.dcache.chimera.quota;

import org.dcache.chimera.ChimeraFsException;
import javax.sql.DataSource;

public class PgsqlQuotaSqlDriver extends QuotaSqlDriver {

	public PgsqlQuotaSqlDriver(DataSource dataSource) throws ChimeraFsException
	{
		super(dataSource);
	}

	private static final String UPDATE_USER_QUOTAS_SQL =
			"UPDATE t_user_quota SET "+
					"ireplica_used = t.replica, "+
					"icustodial_used = t.custodial, "+
					"ioutput_used = t.output "+
					"FROM ( "+
					"SELECT iuid, SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize else 0 end) AS output, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize else 0 end) AS custodial "+
					"FROM t_inodes WHERE iuid IN (SELECT iuid FROM t_user_quota) AND itype=32768 GROUP BY iuid) as t "+
					"WHERE t.iuid = t_user_quota.iuid";

	/**
	 * Update user quotas
	 */
	@Override
	public void updateUserQuota()
	{
		jdbc.update(UPDATE_USER_QUOTAS_SQL);
	}

	private static final String UPDATE_GROUP_QUOTAS_SQL =
			"UPDATE t_group_quota SET "+
					"ireplica_used = t.replica, "+
					"icustodial_used = t.custodial, "+
					"ioutput_used = t.output "+
					"FROM ( "+
					"SELECT igid, SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize else 0 end) AS output, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize else 0 end) AS custodial "+
					"FROM t_inodes WHERE igid IN (SELECT igid FROM t_group_quota) AND itype=32768 GROUP BY igid) as t "+
					"WHERE t.igid = t_group_quota.igid";

    /**
	 * Update group quotas
     */
	@Override
    public void updateGroupQuota()
	{
		jdbc.update(UPDATE_GROUP_QUOTAS_SQL);
    }
}
