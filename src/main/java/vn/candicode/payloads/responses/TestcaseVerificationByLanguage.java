package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestcaseVerificationByLanguage implements Serializable {
    private String language;
    private String output;
    private String runtimeError;
    private Boolean compiled;
    private String compileError;

    public TestcaseVerificationByLanguage(String language, String output, String runtimeError, Boolean compiled, String compileError) {
        this.language = language;
        this.output = output;
        this.runtimeError = runtimeError;
        this.compiled = compiled;
        this.compileError = compileError;
    }
}
