package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import vn.candicode.common.structure.wrapper.Pair;
import vn.candicode.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Log4j2
public class C extends Executor {

    public C(String submissionDir, String compilePath, String runPath, long allowedTime) {
        super(submissionDir, compilePath, runPath, allowedTime);
    }

    public C(String submissionDir, String compilePath, String runPath) {
        super(submissionDir, compilePath, runPath, -1);
    }

    @Override
    public Pair compile() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("chmod", "+x", submissionDir + compilePath);
            processBuilder.directory(new File(root));
            Process process = processBuilder.start();
            process.waitFor();

            process = processBuilder.command(submissionDir + compilePath).start();
            int status = process.waitFor();

            String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            File errorFile = new File(root, FileUtils.ERROR_FILE);

            if (StringUtils.hasText(error)) {
                org.apache.commons.io.FileUtils.writeStringToFile(errorFile, error);
            }

            return new Pair(status == 0, error);
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return new Pair(false, null);
    }

    @Override
    public void run() {

    }
}
