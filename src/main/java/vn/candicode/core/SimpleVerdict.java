package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.utils.FileUtils;

import java.io.File;
import java.io.IOException;

@Log4j2
public class SimpleVerdict {
    private final Executor executor;
    private final String challengeDirPath;

    public SimpleVerdict(String language, String input, String challengeDirPath) {
        this.executor = build(language, input, challengeDirPath);
        this.challengeDirPath = challengeDirPath;
    }

    protected Executor build(String language, String input, String challengeDirPath) {
        if ("java".equals(language.toLowerCase())) {
            return new JavaV2(input, challengeDirPath);
        }

        return null;
    }

    public Object[] verify() {
        VerdictResult compileResult = executor.compile();

        if (compileResult.equals(VerdictResult.CompileFailed)) {
            return new Object[]{false, null};
        }

        executor.run();

        File outputFile = new File(challengeDirPath + File.separator + "out.txt");

        try {
            return new Object[]{true, FileUtils.readFileToString(outputFile)};
        } catch (IOException e) {
            log.error("Error when read output file. Message - {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return new Object[]{true, "File Error"};
    }
}
