package vn.candicode.core.compilers;

import lombok.extern.log4j.Log4j2;
import vn.candicode.core.judges.Timer;

import java.io.*;

@Log4j2
public class JavaCompiler extends LanguageCompiler {
    public JavaCompiler(String file, String content, String dir, int timeout) {
        super(file, content, dir, timeout);
    }

    @Override
    public void compile() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/" + file)));
            out.write(content);
            out.close();

            // Create the compiler script
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/compile.sh")));
            out.write("cd \"" + dir + "\"\n");
            out.write("javac " + file + " 2> err.txt");
            out.close();

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("chmod +x " + dir + "/compile.sh");
            process.waitFor();
            process = runtime.exec(dir + "/compile.sh");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.warn("\n\nError when compiling\n Exception - {}, Message - {}\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void execute() {
        try {
            // Create the execution script
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/run.sh")));
            out.write("cd \"" + dir + "\"\n");
            out.write("chroot .\n");
            out.write("java " + file.substring(0, file.length() - 5) + " < in.txt > out.txt");
            out.close();

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("chmod +x " + dir + "/.run.sh");
            process.waitFor();
            process = runtime.exec(dir + "/run.sh"); // do execute

            Timer timer = new Timer(this, process, 3000);
            timer.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.warn("\n\nError when executing\nException - {}, Message - {}\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
