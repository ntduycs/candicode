package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.common.structure.wrapper.Pair;
import vn.candicode.models.enums.LanguageName;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class Verdict extends Thread {

    private final LanguageName language;
    private final String submissionDir;
    private final String compilePath;
    private final String runPath;
    private final CountDownLatch latch;
    private final boolean hasCompiled;

    public Verdict(LanguageName language, String submissionDir, String compilePath, String runPath, CountDownLatch latch, boolean hasCompiled) {
        this.language = language;
        this.submissionDir = submissionDir;
        this.compilePath = compilePath;
        this.runPath = runPath;
        this.latch = latch;
        this.hasCompiled = hasCompiled;
    }

    @Override
    public void run() {
        Executor executor = getCodeExecutor(language);

        if (!hasCompiled) {
            Pair compileResult = executor.compile();

            if (!compileResult.isCompiled()) {
                log.error("Compile failed");
                return;
            }
        }

        executor.run();

        latch.countDown();
    }

    protected Executor getCodeExecutor(LanguageName language) {
        switch (language) {
            case C:
                return new C();
            case Java:
                return new Java(submissionDir, compilePath, runPath);
            default:
                return null;
        }
    }
}
