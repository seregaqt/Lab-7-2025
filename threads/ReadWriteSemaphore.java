package threads;

public class ReadWriteSemaphore {
    private int activeReaders = 0;
    private int activeWriters = 0;
    private int waitingReaders = 0;
    private int waitingWriters = 0;
    
    public synchronized void beginRead() throws InterruptedException {
        waitingReaders++;
        while (activeWriters > 0 || waitingWriters > 0) {
            wait();
        }
        waitingReaders--;
        activeReaders++;
    }
    
    public synchronized void endRead() {
        activeReaders--;
        if (activeReaders == 0) {
            notifyAll();
        }
    }
    
    public synchronized void beginWrite() throws InterruptedException {
        waitingWriters++;
        while (activeReaders > 0 || activeWriters > 0) {
            wait();
        }
        waitingWriters--;
        activeWriters++;
    }
    
    public synchronized void endWrite() {
        activeWriters--;
        notifyAll();
    }
}