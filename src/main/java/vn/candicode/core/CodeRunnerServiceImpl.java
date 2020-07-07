package vn.candicode.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CodeRunnerServiceImpl implements CodeRunnerService {
    private static final long DEFAULT_TIMEOUT = 3000000000L; // 3s

    private static final List<String> SCRIPTING_LANGUAGES = List.of("python", "js");

    private boolean noNeedToCompile(String language) {
        return SCRIPTING_LANGUAGES.contains(language.toLowerCase());
    }

    @Override
    public CompileResult compile(File root, String language) {
        if (noNeedToCompile(language)) {
            return CompileResult.success(language);
        }

        String error;

        ProcessBuilder pp = new ProcessBuilder();
        Process p;

        // ==========================================================
        // = Grant execution privilege =
        // ==========================================================
        try {
            p = pp.command("chmod", "+x", "compile.sh")
                .directory(root)
                .start();

            p.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Abnormal error happened when run compile task at {} (Execution granting phase, in detail). Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return CompileResult.failure(language, "Internal server error");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Failed to read error stream when run compile task at {}. Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return CompileResult.failure(language, "Internal server error");
        }

        if (StringUtils.hasText(error)) {
            return CompileResult.failure(language, error);
        }

        // ==========================================================
        // = Run compile script =
        // ==========================================================
        try {
            p = pp.command("./compile.sh")
                .directory(root)
                .start();
        } catch (IOException e) {
            log.error("Abnormal error happened when run compile task at {} (Run script phase, in detail). Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return CompileResult.failure(language, "Internal server error");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Failed to read error stream when run compile task at {}. Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return CompileResult.failure(language, "Internal server error");
        }

        if (p.isAlive()) {
            p.destroy();
        }

        if (StringUtils.hasText(error)) {
            return CompileResult.failure(language, error);
        } else {
            return CompileResult.success(language);
        }
    }

    /**
     * @param root
     * @param clock    set to 3s if 0 <= clock <= 3,000,000,000 (nanoseconds)
     * @param language
     * @return
     */
    @Override
    public ExecutionResult run(File root, long clock, String language) {
        String error;
        String output;
        long timeout = (clock > 0 && clock < DEFAULT_TIMEOUT) ? clock : DEFAULT_TIMEOUT;

        ProcessBuilder pp = new ProcessBuilder();
        Process p;

        try {
            p = pp.command("chmod", "+x", "run.sh")
                .directory(root)
                .start();

            p.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Abnormal error happened when run execution task at {} (Granting phase, in detail). Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Failed to read error stream when run compile task at {}. Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        }

        if (StringUtils.hasText(error)) {
            return ExecutionResult.failure(language, error);
        }

        Stopwatch stopwatch;
        try {
            // Start time counter
            stopwatch = Stopwatch.createStarted();

            p = pp.command("./run.sh").directory(root).start();
            boolean exitNormally = p.waitFor(timeout, TimeUnit.NANOSECONDS);

            // Stop time counter
            stopwatch.stop();

            // Check process exceeds given time threshold
            if (!exitNormally) {
                if (p.isAlive()) p.destroy();
                return ExecutionResult.failure(language, "Timeout error");
            }
        } catch (IOException e) {
            log.error("Abnormal error happened when run execution task at {} (Executing phase, in detail). Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        } catch (InterruptedException e) {
            log.error("Interrupted error happened when run execution task at {} (Executing phase, in detail). Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Failed to read error stream when run execution task at {}. Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        }

        long executionTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        if (StringUtils.hasText(error)) {
            return ExecutionResult.failure(language, error, executionTime);
        }

        File out = new File(root, "out.txt");
        if (!out.exists()) {
            return ExecutionResult.failure(language, "Cannot read out.txt file. Please verify that your implementation");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(out)))) {
            output = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Abnormally failed to read output file when run execution task at {}. Message - {}", root.getPath(), e.getMessage());
            e.printStackTrace();
            return ExecutionResult.failure(language, "Internal server error");
        }

        return new ExecutionResult(language, null, null, executionTime, output);
    }

    @Override
    @Async
    public void cleanGarbageFiles(File root, String lang) {
        String extension;

        switch (lang) {
            case "java":
                extension = "class";
                break;
            case "c":
            case "cpp":
                extension = "out";
                break;
            default:
                return;
        }

        String wildcard = "*." + extension;

        List<String> command = Lists.newArrayList("find", ".", "-name", wildcard, "-type", "f", "-delete");

        try {
            new ProcessBuilder(command).directory(root).start();
        } catch (IOException e) {
            log.error("Cannot clean garbage files. Message - {}", e.getLocalizedMessage());
        }
    }
}
