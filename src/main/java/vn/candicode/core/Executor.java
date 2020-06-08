package vn.candicode.core;

import vn.candicode.common.structure.wrapper.Pair;

public abstract class Executor {
    public boolean shouldStop = false;

    public abstract Pair compile();

    public abstract void run();
}
