package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import vn.candicode.common.structure.wrapper.Pair;
import vn.candicode.exceptions.CodeExecutionException;

import java.io.*;

@Log4j2
public class Java extends Executor {
    private final String submissionDir;
    private final long allowedTime;

    public Java(String submissionDir, long allowedTime) {
        this.submissionDir = submissionDir;
        this.allowedTime = allowedTime;
    }

    public Java(String submissionDir) {
        this.submissionDir = submissionDir;
        this.allowedTime = -1;
    }

    @Override
    public void run() {
        genRunShellScript(submissionDir);
        try {
            Runtime terminal = Runtime.getRuntime();
            Process process = terminal.exec("chmod +x " + submissionDir + File.separator + "run.sh");
            process.waitFor();
            process = terminal.exec(submissionDir + File.separator + "run.sh");
//            try (Writer w = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
//                w.write("1|2");
//                w.write("\n");
//                w.write("2|5");
//            }
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
        genCompileShellScript(submissionDir);
        try {
            Runtime terminal = Runtime.getRuntime();
            Process process = terminal.exec("chmod +x " + submissionDir + File.separator + "compile.sh");
            process.waitFor();
            process = terminal.exec(submissionDir + File.separator + "compile.sh");
//            String line;
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            while (( line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            int status = process.waitFor();

            // No need to worry about what error happened when compiling here
            return new Pair(status == 0, null);
        } catch (InterruptedException e) {
            log.error("Thread was suspended accidentally. Message - {}", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            e.printStackTrace();
        }
        return new Pair(false, null);
    }

    private void genCompileShellScript(String submissionDir) {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(submissionDir + File.separator + "compile.sh")))) {
            out.write("cd \"" + submissionDir + File.separator + "src\"\n");
            out.write("javac Main.java 2> ../err.txt");
            out.write("\n");
        } catch (FileNotFoundException e) {
            log.error("Compile shell script is not exist and cannot be created. Message - {}", e.getMessage());
            throw new CodeExecutionException(e.getMessage());
        } catch (IOException e) {
            log.error("I/O error happenned. Message - {}", e.getMessage());
            throw new CodeExecutionException(e.getMessage());
        }
    }

    private void genRunShellScript(String submissionDir) {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(submissionDir + File.separator + "run.sh")))) {
            out.write("cd \"" + submissionDir + File.separator + "src\"\n");
            out.write("java Main < ../in.txt > ../out.txt 2> ../err.txt");
            out.write("\n");
        } catch (FileNotFoundException e) {
            log.error("Run shell script is not exist and cannot be created. Message - {}", e.getMessage());
            throw new CodeExecutionException(e.getMessage());
        } catch (IOException e) {
            log.error("I/O error happened. Message - {}", e.getMessage());
            throw new CodeExecutionException(e.getMessage());
        }
    }

}
