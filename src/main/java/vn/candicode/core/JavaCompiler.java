package vn.candicode.core;

public class JavaCompiler implements Compiler {
    public final String submissionDir;

    public JavaCompiler(String submissionDir) {
        this.submissionDir = submissionDir;
    }

    @Override
    public CompileResult compile() {
//        try {
        Runtime terminal = Runtime.getRuntime();
//            Process p = terminal.exec("chmod +x " + submissionDir + File.separator + "")
//        }
        return null;
    }
}
