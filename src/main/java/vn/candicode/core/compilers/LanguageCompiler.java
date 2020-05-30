package vn.candicode.core.compilers;

public abstract class LanguageCompiler {
    protected boolean timedout = false;

    protected String file;
    protected String content;
    protected String dir;
    protected int timeout;

    public LanguageCompiler(String file, String content, String dir, int timeout) {
        this.file = file;
        this.content = content;
        this.dir = dir;
        this.timeout = timeout;
    }

    public abstract void compile();

    public abstract void execute();

    public boolean isTimedout() {
        return timedout;
    }

    public void setTimedout(boolean timedout) {
        this.timedout = timedout;
    }
}
