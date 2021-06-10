package org.dcache.chimera.quota.spi;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.dcache.chimera.ChimeraFsException;
import org.dcache.chimera.quota.QuotaSqlDriver;

/**
 * SPI interface to Driver provider. Provider must create an instance of
 * {@link QuotaSqlDriver} for a supported DB type.
 */
public interface DbDriverProvider {

    /**
     * Check is provide support specific database type.
     *
     * @param dataSource source for database connection
     * @return true if provider support database type.
     * @throws SQLException on db errors
     */
    boolean isSupportDB(DataSource dataSource) throws SQLException;

    /**
     * Get {@link QuotaSqlDriver} for the specific database.
     *
     * @param dataSource source for database connection
     * @return driver for specific database.
     * @throws SQLException on db errors
     */
    QuotaSqlDriver getDriver(DataSource dataSource) throws SQLException, ChimeraFsException;
}
