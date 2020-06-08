package vn.candicode.workers;

public abstract class Worker {
    public abstract Object[] compile();

    public abstract void run();

    public abstract void match();
}
