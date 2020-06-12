package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TestcaseVerificationResult implements Serializable {
    private boolean validFormat;
    private String validFormatError;
    private String otherError;
    private List<TestcaseVerificationByLanguage> result = new ArrayList<>();
}
