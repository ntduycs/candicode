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
    @Override
    public CompileResult compile(File root) {
        String error = null;

        ProcessBuilder pp = new ProcessBuilder();
        Process p;

        try {
            p = pp.command("chmod", "+x", "compile.sh")
                .directory(root)
                .start();

            p.waitFor();
        } catch (IOException | InterruptedException e) {
            return new CompileResult(false, e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (StringUtils.hasText(error)) {
            return new CompileResult(false, error);
        }

        try {
            p = pp.command("./compile.sh").directory(root).start();
        } catch (IOException e) {
            return new CompileResult(false, e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (p.isAlive()) {
            p.destroy();
        }

        if (StringUtils.hasText(error)) {
            return new CompileResult(false, error);
        } else {
            return new CompileResult(true, null);
        }
    }

    @Override
    public ExecutionResult run(File root, long clock) {
        String error = null;
        String output;

        ProcessBuilder pp = new ProcessBuilder();
        Process p;

        try {
            p = pp.command("chmod", "+x", "run.sh")
                .directory(root)
                .start();

            p.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage());
            return new ExecutionResult(e.getMessage(), null, 0, null);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (StringUtils.hasText(error)) {
            return new ExecutionResult(error, null, 0, null);
        }

        Stopwatch stopwatch;
        Timewatch timewatch = new Timewatch(p, clock);
        try {
            // Start time counter
            stopwatch = Stopwatch.createStarted();
            p = pp.command("./run.sh").directory(root).start();
            if (clock > 0) timewatch.start();
            p.waitFor();
            // Stop time counter
            stopwatch.stop();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return new ExecutionResult(e.getMessage(), null, 0, null);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage());
            return new ExecutionResult(null, e.getMessage(), clock, null);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            error = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }

        long executionTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        if (StringUtils.hasText(error)) {
            return new ExecutionResult(error, null, executionTime, null);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(root, "out.txt"))))) {
            output = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return new ExecutionResult("No output found", null, executionTime, null);
        }

        return new ExecutionResult(null, null, executionTime, output);
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
