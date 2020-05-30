package vn.candicode.core.judges;

import lombok.extern.log4j.Log4j2;
import vn.candicode.core.compilers.LanguageCompiler;

/**
 * This class does execute a command with a timeout period
 */
@Log4j2
public class Timer extends Thread {
    LanguageCompiler compiler;
    Process process;
    long time;

    public Timer(LanguageCompiler compiler, Process process, long time) {
        this.compiler = compiler;
        this.process = process;
        this.time = time;
    }

    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     */
    @Override
    public void run() {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            log.warn("\n\nError when timer is running. Exception - {}, Message - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        // In case that the process has not been terminated yet, exitValue() call will thrown exception
        try {
            process.exitValue();
            compiler.setTimedout(false);
        } catch (IllegalThreadStateException e) {
            compiler.setTimedout(true);
            process.destroy();
        }
    }
}
