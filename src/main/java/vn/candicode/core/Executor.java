package vn.candicode.core;

public abstract class Executor {
    public boolean shouldStop = false;

    public abstract VerdictResult compile();

    public abstract void run();
}
