package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestcaseVerificationResult implements Serializable {
    private boolean validFormat;
    private boolean compiled;
    private String output;
}
