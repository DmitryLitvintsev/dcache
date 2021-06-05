package org.dcache.chimera.quota;

import org.dcache.chimera.ChimeraFsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuotaSqlDriver { 

	private static final Logger LOGGER =
	LoggerFactory.getLogger(QuotaSqlDriver.class);

    final JdbcTemplate jdbc;

    public QuotaSqlDriver(DataSource dataSource)
	throws ChimeraFsException
    {
    	jdbc = new JdbcTemplate(dataSource);
    }


    public static QuotaSqlDriver getDriverInstance(DataSource dataSource) 
	throws ChimeraFsException, SQLException
    {
    	return new QuotaSqlDriver(dataSource);
    }

    /**
       Update user quotas 
     */ 
    private static final String UPDATE_USER_QUOTAS_SQL =
			"UPDATE t_user_quota set "+
					"ireplica_used = t.replica, "+
					"icustodial_used = t.custodial, "+
					"ioutput_used = t.output "+
					"FROM ( "+
					"SELECT iuid, SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize else 0 end) AS output, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize else 0 end) AS custodial "+
					"FROM t_inodes WHERE iuid IN (SELECT iuid FROM t_user_quota) AND itype=32768 GROUP BY iuid) t "+
					"WHERE t.iuid = t_user_quota.iuid";

   public void updateUserQuota() {
	   jdbc.update(UPDATE_USER_QUOTAS_SQL);
   }

    /**
       Update group quotas 
     */ 
    private static final String UPDATE_GROUP_QUOTAS_SQL =
			"UPDATE t_group_quota set "+
					"ireplica_used = t.replica, "+
					"icustodial_used = t.custodial, "+
					"ioutput_used = t.output "+
					"FROM ( "+
					"SELECT igid, SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize else 0 end) AS output, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize else 0 end) AS custodial "+
					"FROM t_inodes WHERE igid IN (SELECT igid FROM t_group_quota) AND itype=32768 GROUP BY igid) t "+
					"WHERE t.igid = t_group_quota.igid";

    public void updateGroupQuota() {
		jdbc.update(UPDATE_GROUP_QUOTAS_SQL);
    }
    
    private static final String SELECT_USER_QUOTAS_SQL =
			"SELECT iuid, "+
					"icustodial_used, icustodial_limit, "+
					"ioutput_used, ioutput_limit, "+
					"ireplica_used, ireplica_limit "+
					"FROM t_user_quota";


    private Long bigDecimalToLong(BigDecimal val) {
		return val == null ? null :
				val.min(BigDecimal.valueOf(Long.MAX_VALUE)).longValue();
    }

    public Map<Integer, Quota> getUserQuotas() {
    	Map<Integer, Quota> quotas = new HashMap<>();
    	jdbc.query(SELECT_USER_QUOTAS_SQL,
		   (rs) -> {
		       int id = rs.getInt("iuid");
		       quotas.put(id,
				  new Quota(id,
						  rs.getLong("icustodial_used"),
						  bigDecimalToLong(rs.getBigDecimal("icustodial_limit")),
						  rs.getLong("ioutput_used"),
						  bigDecimalToLong(rs.getBigDecimal("ioutput_limit")),
						  rs.getLong("ireplica_used"),
						  bigDecimalToLong(rs.getBigDecimal("ireplica_limit"))));
		   });
		return quotas;
    }

    private static final String SELECT_GROUP_QUOTAS_SQL =
			"SELECT igid, "+
					"icustodial_used, icustodial_limit, "+
					"ioutput_used, ioutput_limit, "+
					"ireplica_used, ireplica_limit "+
					"FROM t_group_quota";


	public Map<Integer, Quota> getGroupQuotas() {
		Map<Integer, Quota> quotas = new HashMap<>();
		jdbc.query(SELECT_GROUP_QUOTAS_SQL,
				(rs) -> {
					int id = rs.getInt("igid");
					quotas.put(id, new Quota(id,
							rs.getLong("icustodial_used"),
							bigDecimalToLong(rs.getBigDecimal("icustodial_limit")),
							rs.getLong("ioutput_used"),
							bigDecimalToLong(rs.getBigDecimal("ioutput_limit")),
							rs.getLong("ireplica_used"),
							bigDecimalToLong(rs.getBigDecimal("ireplica_limit"))));
				});
		return quotas;
	}

	private static final String UPDATE_USER_QUOTA_SQL =
			"UPDATE t_user_quota SET "+
					"icustodial_limit = ?, ioutput_limit = ?, ireplica_limit=? "+
					"WHERE iuid = ?";

    public void setUserQuota(Quota q) {
		jdbc.update(UPDATE_USER_QUOTA_SQL,
				q.getUsedCustodialSpaceLimit(),
				q.getUsedOutputSpaceLimit(),
				q.getUsedReplicaSpaceLimit(),
				q.getId());
	}

    private static final String UPDATE_GROUP_QUOTA_SQL =
			"UPDATE t_group_quota SET "+
					"icustodial_limit = ?, ioutput_limit = ?, ireplica_limit=? "+
					"WHERE igid = ?";

    public void setGroupQuota(Quota q) {
		jdbc.update(UPDATE_GROUP_QUOTA_SQL,
				q.getUsedCustodialSpaceLimit(),
				q.getUsedOutputSpaceLimit(),
				q.getUsedReplicaSpaceLimit(),
				q.getId());
	}
}
