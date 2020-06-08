package vn.candicode.common.structure.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Triple implements Serializable {
    private boolean compiled;
    private String compileError;
    private String output;

    public Triple(boolean compiled, String compileError, String output) {
        this.compiled = compiled;
        this.compileError = compileError;
        this.output = output;
    }
}
