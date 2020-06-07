package vn.candicode.workers;

import java.util.concurrent.CountDownLatch;

public class Examiner extends Thread {

    private final CountDownLatch doneSignal;
    private final String submissionDir;
    private final Worker worker;
    private final Result result;

    public Examiner(CountDownLatch doneSignal, String submissionDir, String language, Result result) {
        this.doneSignal = doneSignal;
        this.submissionDir = submissionDir;
        this.worker = buildWorker(language);
        this.result = result;
    }

    @Override
    public void run() {
        Object[] compileResult = worker.compile();
        boolean compiled = (boolean) compileResult[0];

        if (!compiled) {
            String error = (String) compileResult[1];
            result.setCompiled(false);
            result.setCompileMessage(error);
            return;
        }

        worker.run();

        worker.match();

        doneSignal.countDown();
    }

    protected Worker buildWorker(String language) {
        if ("Java".equals(language)) {
            return new JavaWorker();
        }

        return null;
    }
}
