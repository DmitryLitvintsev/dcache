package org.dcache.tests.util;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import diskCacheV111.util.JobScheduler;
import diskCacheV111.util.SimpleJobScheduler;

public class JobSchedulerTest {

    public static class ExampleJob implements Runnable {

        private final CountDownLatch _doneCounter;
        private final CountDownLatch _startCounter;
        private final String _name;
        private final long _waitTime;
        private AtomicBoolean _isInterrupted = new AtomicBoolean(false);

        public ExampleJob(String name, CountDownLatch startCounter, CountDownLatch doneCounter, long waitTime) {
            _name = name;
            _startCounter = startCounter;
            _doneCounter = doneCounter;
            _waitTime = waitTime;
        }

        @Override
        public void run() {
            try {
                if (_startCounter != null) {
                    _startCounter.countDown();
                }
                Thread.sleep(_waitTime);
            } catch (InterruptedException ie) {
                _isInterrupted.set(true);
            } finally {
                if (_doneCounter != null) {
                    _doneCounter.countDown();
                }
            }
        }

        public boolean isInterrupted() {
            return _isInterrupted.get();
        }

        @Override
        public String toString() {
            return _name;
        }
    }
    private JobScheduler _jobScheduler;

    @Before
    public void setUp()
    {
        _jobScheduler = new SimpleJobScheduler("scheduler");
    }

    @Test
    public void testSimpleJobQueue() throws InvocationTargetException, InterruptedException {

        int jobsCount = 5;
        long waitTime = 500;
        CountDownLatch doneCounter = new CountDownLatch(jobsCount);

        _jobScheduler.setMaxActiveJobs(jobsCount);

        // Fill queue
        for (int i = 0; i < jobsCount; i++) {
            _jobScheduler.add(new ExampleJob("S-" + i, null, doneCounter, waitTime));
        }

        assertTrue("not all jobs are done", doneCounter.await(2 * waitTime * jobsCount, TimeUnit.MILLISECONDS));
        assertTrue("job queue is not empty", _jobScheduler.getQueueSize() == 0);

    }

    @Test
    public void testKillJob() throws Exception {

        CountDownLatch startCounter = new CountDownLatch(1);
        CountDownLatch doneCounter = new CountDownLatch(1);
        ExampleJob job = new ExampleJob("job to kill", startCounter, doneCounter, 10000);

        int jobId = _jobScheduler.add(job);

        startCounter.await();
        _jobScheduler.kill(jobId, true);


        doneCounter.await();

        assertTrue("Job is not interrupted", job.isInterrupted());
    }
}
