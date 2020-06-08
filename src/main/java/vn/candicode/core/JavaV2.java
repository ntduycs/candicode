package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class JavaV2 extends Executor {
    private final String input;
    private final String challengeDir;

    public JavaV2(String input, String challengeDir) {
        this.input = input;
        this.challengeDir = challengeDir;
    }

    @Override
    public VerdictResult compile() {
        try {
            File inputFile = new File(challengeDir + File.separator + "in.txt");

            if (!inputFile.exists()) {
                Files.createFile(Paths.get(inputFile.getAbsolutePath()));
            }

            String fakeinput = "2 2 " + input;

            FileUtils.overwriteFile(inputFile, fakeinput);

            Runtime terminal = Runtime.getRuntime();
            Process process = terminal.exec("chmod +x " + challengeDir + File.separator + "compile.sh");
            process.waitFor();
            process = terminal.exec(challengeDir + File.separator + "compile.sh");
            int status = process.waitFor();

            return status == 0 ? VerdictResult.CompileSuccess : VerdictResult.CompileFailed;
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
        }
        return VerdictResult.CompileFailed;
    }

    @Override
    public void run() {
        try {
            Runtime terminal = Runtime.getRuntime();
            Process process = terminal.exec("chmod +x " + challengeDir + File.separator + "run.sh");
            process.waitFor();
            process = terminal.exec(challengeDir + File.separator + "run.sh");

            process.waitFor();
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
