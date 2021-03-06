package org.dcache.pool.repository.meta.db;

import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

import com.sleepycat.je.EnvironmentFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import diskCacheV111.util.CacheException;
import diskCacheV111.util.PnfsId;
import diskCacheV111.vehicles.StorageInfo;

import org.dcache.pool.repository.DuplicateEntryException;
import org.dcache.pool.repository.FileStore;
import org.dcache.pool.repository.MetaDataRecord;
import org.dcache.pool.repository.MetaDataStore;

/**
 * BerkeleyDB based MetaDataRepository implementation.
 *
 * The database is stored in a subdirectory of the pool directory
 * called 'meta'.
 *
 * The cache repository entries generated by this store fetch storage
 * info from the database on demand and caches them using a
 * SoftReference.
 */
public class BerkeleyDBMetaDataRepository
    implements MetaDataStore
{
    private static Logger _log =
        LoggerFactory.getLogger(BerkeleyDBMetaDataRepository.class);

    private static final String DIRECTORY_NAME = "meta";

    /**
     * The file store for which we hold the meta data.
     */
    private final FileStore _fileStore;

    /**
     * The BerkeleyDB database to use.
     */
    private final MetaDataRepositoryDatabase _database;

    /**
     * The BerkeleyDB database to use.
     */
    private final MetaDataRepositoryViews _views;

    /**
     * Directory containing the database.
     */
    private final File _dir;

    /**
     * Opens a BerkeleyDB based meta data repository. If the database
     * does not exist yet, then it is created. If the 'meta' directory
     * does not exist, it is created.
     */
    public BerkeleyDBMetaDataRepository(FileStore fileStore,
                                        File directory)
            throws FileNotFoundException, DatabaseException, CacheException
    {
        this(fileStore, directory, false);
    }

    public BerkeleyDBMetaDataRepository(FileStore fileStore,
                                        File directory,
                                        boolean readOnly)
            throws FileNotFoundException, DatabaseException, CacheException
    {
        _fileStore = fileStore;
        _dir = new File(directory, DIRECTORY_NAME);

        if (!_dir.exists()) {
            if (!_dir.mkdir()) {
                throw new FileNotFoundException("Failed to create directory: " + _dir);
            }
        } else if (!_dir.isDirectory()) {
            throw new FileNotFoundException("No such directory: " + _dir);
        }

        try {
            _database = new MetaDataRepositoryDatabase(_dir, readOnly);
            _views = new MetaDataRepositoryViews(_database);
        } catch (EnvironmentFailureException e) {
            throw new CacheException(CacheException.PANIC, "Failed to open Berkeley DB database. When upgrading to " +
                    "dCache 2.6, it may be necessary to run the /usr/sbin/dcache-pool-meta-preupgrade utility " +
                    "before starting the pool. If that does not resolve the problem, you should contact " +
                    "support@dcache.org", e);
        }
    }

    @Override
    public Collection<PnfsId> list()
    {
        Set<PnfsId> ids = new HashSet<>();
        for (Object id: _views.getStorageInfoMap().keySet()) {
            ids.add(new PnfsId((String) id));
        }
        for (Object id: _views.getStateMap().keySet()) {
            ids.add(new PnfsId((String) id));
        }
        return ids;
    }

    @Override
    public MetaDataRecord get(PnfsId id)
    {
        return CacheRepositoryEntryImpl.load(this, id);
    }

    /**
     * TODO: The entry is not persistent yet!
     */
    @Override
    public MetaDataRecord create(PnfsId id)
        throws DuplicateEntryException
    {
        /* CacheRepositoryEntryImpl.load silently drops incomplete
         * entries. To conform to the contract of the MetaDataStore
         * interface, we need to check whether both records are
         * present in the database.
         */
        if (get(id) != null) {
            throw new DuplicateEntryException(id);
        }
        return new CacheRepositoryEntryImpl(this, id);
    }

    @Override
    public MetaDataRecord create(MetaDataRecord entry)
        throws DuplicateEntryException, CacheException
    {
        /* CacheRepositoryEntryImpl.load silently drops incomplete
         * entries. To conform to the contract of the MetaDataStore
         * interface, we need to check whether both records are
         * present in the database.
         */
        PnfsId id = entry.getPnfsId();
        if (get(id) != null) {
            throw new DuplicateEntryException(id);
        }
        return new CacheRepositoryEntryImpl(this, entry);
    }

    @Override
    public void remove(PnfsId id)
    {
        _views.getStorageInfoMap().remove(id.toString());
        _views.getStateMap().remove(id.toString());
    }

    @Override
    public synchronized boolean isOk()
    {
        File tmp = new File(_dir, ".repository_is_ok");
        try {
            tmp.delete();
            tmp.deleteOnExit();

            if (!tmp.createNewFile() || !tmp.exists()) {
                _log.error("Could not create " + tmp);
                return false;
            }

            if (_database.isFailed()) {
                return false;
            }

            return true;
	} catch (IOException e) {
            _log.error("Failed to touch " + tmp + ": " + e.getMessage());
            return false;
	}
    }

    /**
     * Requests a data file from the CacheRepository. Used by the
     * entries to obtain a data file.
     */
    File getDataFile(PnfsId id)
    {
        return _fileStore.get(id);
    }

    /**
     * Returns a database backed map of all StorageInfo objects.
     */
    StoredMap<String,StorageInfo> getStorageInfoMap()
    {
        return _views.getStorageInfoMap();
    }

    /**
     * Returns a database backed map of all state objects.
     */
    StoredMap<String,CacheRepositoryEntryState> getStateMap()
    {
        return _views.getStateMap();
    }

    /** Closes the database. */
    @Override
    public void close()
    {
        try {
            _database.close();
        } catch (DatabaseException e) {
            _log.error("Ignored: Could not close database: " + e.getMessage());
        }
    }

    /**
     * Returns the path
     */
    @Override
    public String toString()
    {
        return _dir.toString();
    }

    /**
     * Provides the amount of free space on the file system containing
     * the data files.
     */
    @Override
    public long getFreeSpace()
    {
        return _fileStore.getFreeSpace();
    }

    /**
     * Provides the total amount of space on the file system
     * containing the data files.
     */
    @Override
    public long getTotalSpace()
    {
        return _fileStore.getTotalSpace();
    }
}
