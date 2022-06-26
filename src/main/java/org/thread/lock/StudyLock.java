package org.thread.lock;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Steven
 */
public class StudyLock implements Lock {

    Sync sync;

    public StudyLock() {
        sync = new Sync();
    }

    @Override
    public void lock() {
        sync.acquire();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release();
    }

    @Override
    public Condition newCondition() {
        return null;
    }


    class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire() {
            return owner.compareAndSet(null, Thread.currentThread());
        }

        @Override
        public boolean tryRelease() {
            return owner.compareAndSet(Thread.currentThread(), null);
        }
    }


    abstract class AbstractQueuedSynchronizer {

        LinkedBlockingQueue<Thread> blockingQueue = new LinkedBlockingQueue<>();
        AtomicReference<Thread> owner = new AtomicReference<>();
        AtomicInteger state = new AtomicInteger(0);

        public AtomicInteger getState() {
            return state;
        }

        public void setState(AtomicInteger state) {
            this.state = state;
        }

        public final void acquire() {
            boolean flag = false;
            while (!tryAcquire()) {
                if (!false) {
                    blockingQueue.offer(Thread.currentThread());
                    flag = true;
                } else {
                    LockSupport.park();
                }

            }
            blockingQueue.remove(Thread.currentThread());
        }

        protected boolean tryAcquire() {
            throw new UnsupportedOperationException();
        }

        public void release() {

            while (tryRelease()) {
                Iterator<Thread> iterator = blockingQueue.iterator();
                while (iterator.hasNext()) {
                    Thread next = iterator.next();
                    LockSupport.unpark(next);
                }
            }

        }

        protected boolean tryRelease() {
            throw new UnsupportedOperationException();
        }
    }
}
