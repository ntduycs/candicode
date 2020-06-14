package vn.candicode.core;

import vn.candicode.common.structure.wrapper.Pair;

import java.io.File;

public abstract class Executor {
    public boolean shouldStop = false;

    protected final String submissionDir;
    protected final String compilePath;
    protected final String runPath;
    protected final long allowedTime;
    protected final String root;

    protected Executor(String submissionDir, String compilePath, String runPath, long allowedTime) {
        this.submissionDir = submissionDir;
        this.compilePath = compilePath;
        this.runPath = runPath;
        this.allowedTime = allowedTime;
        this.root = submissionDir + compilePath.substring(0, compilePath.lastIndexOf(File.separator));
    }

    public abstract Pair compile();

    public abstract void run();
}
