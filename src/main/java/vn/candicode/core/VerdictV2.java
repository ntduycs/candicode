package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.models.enums.LanguageName;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class VerdictV2 extends Thread {
    private final LanguageName language;
    private final String submissionDirPath;
    private final CountDownLatch latch;
    private CompileResult result;

    public VerdictV2(LanguageName language, String submissionDirPath, CountDownLatch latch, CompileResult result) {
        this.language = language;
        this.submissionDirPath = submissionDirPath;
        this.latch = latch;
        this.result = result;
    }

    @Override
    public void run() {
        Compiler compiler = getCompiler(language);

        this.result = compiler.compile();
    }

    protected Compiler getCompiler(LanguageName language) {
        switch (language) {
            case Java:
                return new JavaCompiler(submissionDirPath);
            default:
                return null;
        }
    }
}
