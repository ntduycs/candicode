package vn.candicode.core.compilers;

import lombok.extern.log4j.Log4j2;
import vn.candicode.core.judges.Timer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Log4j2
public class PythonCompiler extends LanguageCompiler {
    public PythonCompiler(String file, String content, String dir, int timeout) {
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
            out.write("cd \"" + dir +"\"\n");
            out.write("chroot .\n");
            out.write("python " + file + " < in.txt > out.txt 2> err.txt");
            out.close();

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("chmod +x " + dir + "/compile.sh");
            process.waitFor();
            process = runtime.exec(dir + "/compile.sh"); // execute the compiler script

            Timer timer = new Timer(this, process, 3000);
            timer.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.warn("\n\nError when compiling\n Exception - {}, Message - {}\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void execute() {
        // Nothing to be done here because Python is a scripting language
    }
}
