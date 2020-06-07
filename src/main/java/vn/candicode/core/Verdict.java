package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.models.enums.LanguageName;

import java.util.concurrent.CountDownLatch;

import static vn.candicode.core.VerdictResult.CompileFailed;

@Log4j2
public class Verdict extends Thread {

    private final LanguageName language;
    private final String submissionDir;
    private final CountDownLatch latch;

    public Verdict(LanguageName language, String submissionDir, CountDownLatch latch) {
        this.language = language;
        this.submissionDir = submissionDir;
        this.latch = latch;
    }

    @Override
    public void run() {
        Executor executor = getCodeExecutor(language);

        VerdictResult compileResult = executor.compile();

        if (compileResult.equals(CompileFailed)) {
            log.error("Compile failed");
            return;
        }

        log.info("Compile success");
        executor.run();

        latch.countDown();
    }

    protected Executor getCodeExecutor(LanguageName language) {
        switch (language) {
            case C:
                return new C();
            case Java:
                return new Java(submissionDir);
            default:
                return null;
        }
    }
}
