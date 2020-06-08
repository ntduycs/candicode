package vn.candicode.common.structure.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Pair implements Serializable {
    private boolean compiled;
    private String compileError;

    public Pair(boolean compiled, String compileError) {
        this.compiled = compiled;
        this.compileError = compileError;
    }
}
