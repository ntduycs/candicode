package vn.candicode.core;

public class Timewatch extends Thread {
    Process p;
    long clock; // in millis

    /**
     * Allocates a new {@code Thread} object.
     * Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public Timewatch(Process p, long clock) {
        this.p = p;
        this.clock = clock;
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            sleep(clock);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // This code block will raise exception if the given process has not yet terminated.
        // If so, force to kill it
        try {
            p.exitValue();
        } catch (IllegalThreadStateException e) {
            p.destroy();
        }
    }
}
