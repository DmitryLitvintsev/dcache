package org.dcache.pool.migration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.SettableFuture;
import diskCacheV111.util.CacheException;
import diskCacheV111.util.LockedCacheException;
import diskCacheV111.util.PnfsId;
import diskCacheV111.vehicles.PoolManagerPoolInformation;
import dmg.cells.nucleus.CellEndpoint;
import dmg.cells.nucleus.CellPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.dcache.cells.CellStub;
import org.dcache.pool.repository.ReplicaState;
import org.dcache.vehicles.FileAttributes;
import org.junit.Before;
import org.junit.Test;

/**
 * Verifies that a target pool rejecting a {@link PoolMigrationCopyReplicaMessage} with
 * {@link CacheException#LOCKED} causes the migration task to fail permanently right away,
 * instead of trying other known replica locations.
 * <p>
 * This can happen routinely for "hot" files that are concurrently being replicated to/from
 * several pools: two pools may simultaneously try to migrate the same file to each other, and
 * each check will reject the other's request with LOCKED to avoid a race condition. Falling
 * back to another location in that case simply masks the underlying race and lets the job
 * spin, so a task in the {@code UpdatingExistingFile} state that receives a LOCKED failure must
 * fail fast ({@code failPermanently}) rather than trying the next known location (see Task.sm).
 */
public class MigrationModuleLockedTargetTest {

    private TaskCompletionHandler handler;
    private CellStub poolStub;
    private ScheduledExecutorService executor;
    private RefreshablePoolList poolList;
    private List<SettableFuture<PoolMigrationCopyReplicaMessage>> sentRequests;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        handler = mock(TaskCompletionHandler.class);
        poolStub = mock(CellStub.class);
        executor = mock(ScheduledExecutorService.class);
        poolList = mock(RefreshablePoolList.class);
        sentRequests = new ArrayList<>();

        // Run everything inline so the test is fully deterministic.
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        PoolManagerPoolInformation pool1 = mock(PoolManagerPoolInformation.class);
        when(pool1.getName()).thenReturn("pool1");
        PoolManagerPoolInformation pool2 = mock(PoolManagerPoolInformation.class);
        when(pool2.getName()).thenReturn("pool2");

        when(poolList.getPools()).thenReturn(ImmutableList.of(pool1, pool2));
        when(poolList.getOfflinePools()).thenReturn(ImmutableList.of());

        when(poolStub.send(any(CellPath.class), any(PoolMigrationCopyReplicaMessage.class),
              any(CellEndpoint.SendFlag[].class)))
              .thenAnswer(invocation -> {
                  SettableFuture<PoolMigrationCopyReplicaMessage> future = SettableFuture.create();
                  sentRequests.add(future);
                  return future;
              });
    }

    @Test
    public void taskShouldFailPermanentlyWhenTargetReportsLocked() {
        PnfsId pnfsId = new PnfsId("000000000000000000000001");

        FileAttributes fileAttributes = new FileAttributes();
        fileAttributes.setLocations(List.of("pool1", "pool2"));
        fileAttributes.setPnfsId(pnfsId);

        TaskParameters parameters = new TaskParameters(
              poolStub,
              mock(CellStub.class),
              mock(CellStub.class),
              executor,
              mock(PoolSelectionStrategy.class),
              poolList,
              false, // isEager
              false, // isMetaOnly
              false, // computeChecksumOnUpdate
              false, // forceSourceMode
              false, // maintainAtime
              1,     // replicas
              false  // waitForTargets
        );

        Task task = new Task(parameters, handler, "sourcePool", pnfsId,
              ReplicaState.CACHED, Collections.emptyList(), Collections.emptyList(),
              fileAttributes, 0L);

        task.run();

        // The task should have contacted the first known location (pool1).
        assertRequestCount(1);

        // Simulate pool1 rejecting the request because it currently has a local migration
        // task active for this file (MigrationModuleServer throws LockedCacheException).
        sentRequests.get(0).setException(
              new LockedCacheException("Target file is busy"));

        // A LOCKED failure must fail the task permanently right away, without trying pool2 -
        // trying other locations only masks the underlying race between concurrent migrations.
        assertRequestCount(1);
        verify(handler, never()).taskFailed(any(), anyInt(), anyString());
        verify(handler).taskFailedPermanently(any(), anyInt(), anyString());
    }

    private void assertRequestCount(int expected) {
        if (sentRequests.size() != expected) {
            throw new AssertionError(
                  "Expected " + expected + " copy request(s) to have been sent, but "
                        + sentRequests.size() + " were sent. Known target locations: pool1, pool2. "
                        + "A LOCKED failure must fail the task permanently instead of trying "
                        + "other known locations.");
        }
    }
}
