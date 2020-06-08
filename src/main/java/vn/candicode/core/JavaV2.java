package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.common.structure.wrapper.Pair;
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
    public Pair compile() {
        try {
            File inputFile = new File(challengeDir + File.separator + "in.txt");

            if (!inputFile.exists()) {
                Files.createFile(Paths.get(inputFile.getAbsolutePath()));
            }

            FileUtils.overwriteFile(inputFile, input);

            Runtime terminal = Runtime.getRuntime();
            Process process = terminal.exec("chmod +x " + challengeDir + File.separator + "compile.sh");
            process.waitFor();
            process = terminal.exec(challengeDir + File.separator + "compile.sh");
            int status = process.waitFor();

            if (status == 0) {
                return new Pair(true, null);
            } else {
                String compileError = FileUtils.readFileToString(new File(challengeDir + File.separator + "err.txt"));
                return new Pair(false, compileError);
            }
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
            return new Pair(false, e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
            return new Pair(false, e.getLocalizedMessage());
        }
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
