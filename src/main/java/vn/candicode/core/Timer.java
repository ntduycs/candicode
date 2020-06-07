package vn.candicode.core;

public class Timer extends Thread {
    Executor executor;
    Process process;
    long allowedTime;

    /**
     * Allocates a new {@code Thread} object. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public Timer(Executor executor, Process process, long allowedTime) {
        this.executor = executor;
        this.process = process;
        this.allowedTime = allowedTime;
    }

    /**
     * This thread will be in pending until the allowedTime has been passed.
     * Then, if the code executing process has not terminated yet, it will be killed intermediately
     */
    @Override
    public void run() {
        try {
            sleep(allowedTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            process.exitValue();
            executor.shouldStop = false;
        } catch (IllegalThreadStateException e) {
            executor.shouldStop = true;
            process.destroy();
        }
    }
}
