package vn.candicode.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CompileResult implements Serializable {
    private final boolean compiled;
    private final String compileError;

    @Override
    public String toString() {
        return "CompileResult{" +
            "compiled=" + compiled +
            ", compileError='" + compileError + '\'' +
            '}';
    }
}
