package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;
import vn.candicode.common.structure.wrapper.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Log4j2
public class Java extends Executor {
    private final String submissionDir;
    private final String compilePath;
    private final String runPath;
    private final long allowedTime;

    public Java(String submissionDir, String compilePath, String runPath, long allowedTime) {
        this.submissionDir = submissionDir;
        this.compilePath = compilePath;
        this.runPath = runPath;
        this.allowedTime = allowedTime;
    }

    public Java(String submissionDir, String compilePath, String runPath) {
        this.submissionDir = submissionDir;
        this.compilePath = compilePath;
        this.runPath = runPath;
        this.allowedTime = -1;
    }

    @Override
    public void run() {
        String root = submissionDir + compilePath.substring(0, compilePath.lastIndexOf(File.separator));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("chmod", "+x", submissionDir + runPath);
            processBuilder.directory(new File(root));
            Process process = processBuilder.start();
            process.waitFor();
            process = processBuilder.command(submissionDir + runPath).start();
            if (allowedTime > -0) {
                new Timer(this, process, allowedTime).start();
            }

            process.waitFor();
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Pair compile() {
        String root = submissionDir + compilePath.substring(0, compilePath.lastIndexOf(File.separator));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("chmod", "+x", submissionDir + compilePath);
            processBuilder.directory(new File(root));
            Process process = processBuilder.start();
            process.waitFor();
            process = processBuilder.command(submissionDir + compilePath).start();
            int status = process.waitFor();

            InputStream errorStream = process.getErrorStream();

            String error = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            File errorFile = new File(root + File.separator + vn.candicode.utils.FileUtils.ERROR_FILE);

            if (StringUtils.hasText(error)) {
                FileUtils.writeStringToFile(errorFile, error);
            }

            // No need to worry about what error happened when compiling here
            return new Pair(status == 0, error);
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
        }
        return new Pair(false, null);
    }

}
