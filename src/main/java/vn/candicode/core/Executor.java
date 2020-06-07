package vn.candicode.core;

public abstract class Executor {
    public boolean shouldStop = false;

    public abstract Verdict.Result compile();

    public abstract void run();
}
