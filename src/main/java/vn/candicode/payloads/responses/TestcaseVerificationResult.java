package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestcaseVerificationResult implements Serializable {
    private boolean validFormat;
    private String validFormatError;
    private boolean compiled;
    private String compileError;
    private String output;
}
