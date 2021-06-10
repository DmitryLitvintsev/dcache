package org.dcache.chimera.quota.spi;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.dcache.chimera.ChimeraFsException;
import org.dcache.chimera.quota.QuotaSqlDriver;
import org.dcache.chimera.quota.PgsqlQuotaSqlDriver;
import static org.dcache.util.SqlHelper.tryToClose;

public class PgsqlDbDriverProvider implements DbDriverProvider {

    @Override
    public boolean isSupportDB(DataSource dataSource)
            throws SQLException
    {
        Connection dbConnection = null;
        try {
            dbConnection = dataSource.getConnection();
            String databaseProductName = dbConnection.getMetaData().getDatabaseProductName();
            return databaseProductName.equalsIgnoreCase("PostgreSQL");
        } finally {
            tryToClose(dbConnection);
        }
    }

    @Override
    public QuotaSqlDriver getDriver(DataSource dataSource)
            throws SQLException, ChimeraFsException
    {
        return new PgsqlQuotaSqlDriver(dataSource);
    }
}
