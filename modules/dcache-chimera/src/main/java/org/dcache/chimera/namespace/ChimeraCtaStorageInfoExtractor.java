package org.dcache.chimera.namespace;

import com.google.common.collect.ImmutableList;
import diskCacheV111.util.AccessLatency;
import diskCacheV111.util.CacheException;
import diskCacheV111.util.FileNotFoundCacheException;
import diskCacheV111.util.RetentionPolicy;
import diskCacheV111.vehicles.CtaStorageInfo;
import diskCacheV111.vehicles.StorageInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dcache.chimera.ChimeraFsException;
import org.dcache.chimera.FileState;
import org.dcache.chimera.StorageGenericLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChimeraCtaStorageInfoExtractor extends ChimeraHsmStorageInfoExtractor {


    private static final Logger LOGGER = LoggerFactory.getLogger(
          ChimeraHsmStorageInfoExtractor.class);


    public ChimeraCtaStorageInfoExtractor(AccessLatency defaultAL,
                                          RetentionPolicy defaultRP) {
        super(defaultAL, defaultRP);
    }


    @Override
    public StorageInfo getFileStorageInfo(ExtendedInode inode) throws CacheException {
        try {

            CtaStorageInfo parentStorageInfo = (CtaStorageInfo) getDirStorageInfo(inode);

            List<String> locations = inode.
                getLocations(StorageGenericLocation.TAPE);

            if (locations.isEmpty()) {
                if (inode.statCache().getState() != FileState.CREATED) {
                    parentStorageInfo.setIsNew(false);
                }
                return parentStorageInfo;
            } else {
                StorageInfo info = new CtaStorageInfo(parentStorageInfo.getStorageGroup(),
                                                      parentStorageInfo.getFileFamily());
                info.setIsNew(false);
                for (String location : locations) {
                    try {
                        info.addLocation(new URI(location));
                    } catch (URISyntaxException e) {
                        LOGGER.debug("Ignoring bad tape location {}: {}",
                                     location, e.toString());
                    }
                }
                return info;
            }
        } catch (ChimeraFsException e) {
            throw new CacheException(e.getMessage());
        }
    }

    @Override
    public StorageInfo getDirStorageInfo(ExtendedInode inode) throws CacheException {
        ExtendedInode directory = inode.isDirectory() ?
            inode : inode.getParent();

        if (directory == null) {
            throw new FileNotFoundCacheException("file unlinked");
        }

        ImmutableList<String> group = directory.getTag("storage_group");
        ImmutableList<String> family = directory.getTag("file_family");

        String sg = getFirstLine(group).map(String::intern).orElse("none");
        String ff = getFirstLine(family).map(String::intern).orElse("none");
        CtaStorageInfo info = new CtaStorageInfo(sg, ff);
        return info;
    }
}
