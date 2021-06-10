package org.dcache.chimera.quota;

import org.dcache.chimera.ChimeraFsException;
import org.dcache.chimera.quota.spi.DbDriverProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class QuotaSqlDriver { 

	private static final Logger LOGGER =
	LoggerFactory.getLogger(QuotaSqlDriver.class);

	private static final ServiceLoader<DbDriverProvider> ALL_PROVIDERS
			= ServiceLoader.load(DbDriverProvider.class);

    final JdbcTemplate jdbc;

    public QuotaSqlDriver(DataSource dataSource)
	throws ChimeraFsException
    {
    	jdbc = new JdbcTemplate(dataSource);
    }

	public static QuotaSqlDriver getDriverInstance(DataSource dataSource)
	throws ChimeraFsException, SQLException
    {
  		for (DbDriverProvider driverProvider: ALL_PROVIDERS) {
			if (driverProvider.isSupportDB(dataSource)) {
				QuotaSqlDriver driver = driverProvider.getDriver(dataSource);
				LOGGER.info("Using DBDriverProvider for Quota: {}", driver.getClass().getName());
				return driver;
			}
		}
		return new QuotaSqlDriver(dataSource);
    }

    private static final String UPDATE_USER_QUOTAS_SQL =
			"MERGE INTO t_user_quota "+
					"USING (SELECT "+
					"iuid, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize ELSE 0 END) AS custodial, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize ELSE 0 END) AS output, "+
					"SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica "+
					"FROM t_inodes WHERE itype=32768 "+
					"AND iuid IN (SELECT iuid FROM t_user_quota) "+
					"GROUP BY iuid) AS t(iuid, custodial, output, replica) "+
					"ON t.iuid = t_user_quota.iuid "+
					"WHEN MATCHED THEN UPDATE SET "+
					"t_user_quota.icustodial_used = t.custodial, "+
					"t_user_quota.ioutput_used = t.output, "+
					"t_user_quota.ireplica_used = t.replica "+
					"WHEN NOT MATCHED THEN INSERT "+
					"(iuid, icustodial_used, ioutput_used, ireplica_used) "+
					"VALUES (t.iuid, t.custodial, t.output, t.replica)";
    /**
       Update user quotas
     */
   public void updateUserQuota() {
	   jdbc.update(UPDATE_USER_QUOTAS_SQL);
   }

	private static final String UPDATE_GROUP_QUOTAS_SQL =
			"MERGE INTO t_group_quota "+
					"USING (SELECT "+
					"igid, "+
					"SUM(CASE WHEN iretention_policy = 0 THEN isize ELSE 0 END) AS custodial, "+
					"SUM(CASE WHEN iretention_policy = 1 THEN isize ELSE 0 END) AS output, "+
					"SUM(CASE WHEN iretention_policy = 2 THEN isize ELSE 0 END) AS replica "+
					"FROM t_inodes WHERE itype=32768 "+
					"AND igid IN (SELECT igid FROM t_group_quota) "+
					"GROUP BY igid) AS t(igid, custodial, output, replica) "+
					"ON t.igid = t_group_quota.igid "+
					"WHEN MATCHED THEN UPDATE SET "+
					"t_group_quota.icustodial_used = t.custodial, "+
					"t_group_quota.ioutput_used = t.output, "+
					"t_group_quota.ireplica_used = t.replica "+
					"WHEN NOT MATCHED THEN INSERT "+
					"(igid, icustodial_used, ioutput_used, ireplica_used) "+
					"VALUES (t.igid, t.custodial, t.output, t.replica)";
    /**
       Update group quotas 
     */
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
		LOGGER.info("getUserQuotas");
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
    	LOGGER.info("getUserQuotas, found {}", quotas.size());
		return quotas;
    }

    private static final String SELECT_GROUP_QUOTAS_SQL =
			"SELECT igid, "+
					"icustodial_used, icustodial_limit, "+
					"ioutput_used, ioutput_limit, "+
					"ireplica_used, ireplica_limit "+
					"FROM t_group_quota";


	public Map<Integer, Quota> getGroupQuotas() {
		LOGGER.info("getGroupQuotas");
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
		LOGGER.info("getGroupQuotas, found {}", quotas.size());
		return quotas;
	}

	private static final String UPDATE_USER_QUOTA_SQL =
			"UPDATE t_user_quota SET "+
					"icustodial_limit = ?, ioutput_limit = ?, ireplica_limit=? "+
					"WHERE iuid = ?";

    public void setUserQuota(Quota q) {
		setQuota(UPDATE_USER_QUOTA_SQL, q);
	}

    private static final String UPDATE_GROUP_QUOTA_SQL =
			"UPDATE t_group_quota SET "+
					"icustodial_limit = ?, ioutput_limit = ?, ireplica_limit=? "+
					"WHERE igid = ?";

    public void setGroupQuota(Quota q) {
		setQuota(UPDATE_GROUP_QUOTA_SQL, q);
	}

	public void setQuota(String query, Quota q) {
    	jdbc.update(query,
				q.getCustodialSpaceLimit(),
				q.getOutputSpaceLimit(),
				q.getReplicaSpaceLimit(),
				q.getId());
	}
	private static final String INSERT_USER_QUOTA =
			"INSERT INTO t_user_quota (iuid, icustodial_used, ioutput_used, ireplica_used, "+
					"icustodial_limit, ioutput_limit, ireplica_limit) "+
					"VALUES (?, 0, 0, 0, ?, ?, ?)";

    public void createUserQuota(Quota q) {
		createQuota(INSERT_USER_QUOTA, q);
	}

	private static final String INSERT_GROUP_QUOTA =
			"INSERT INTO t_group_quota (igid, icustodial_used, ioutput_used, ireplica_used, " +
					"icustodial_limit, ioutput_limit, ireplica_limit) "+
					"VALUES (?, 0, 0, 0, ?, ?, ?)";

    public void createGroupQuota(Quota q) {
		createQuota(INSERT_GROUP_QUOTA, q);
	}

	private void createQuota(String query, Quota q) {
		jdbc.update(query,
				q.getId(),
				q.getCustodialSpaceLimit(),
				q.getOutputSpaceLimit(),
				q.getReplicaSpaceLimit());

	}
}
