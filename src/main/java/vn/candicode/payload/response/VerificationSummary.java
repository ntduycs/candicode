package vn.candicode.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * Testcase verification result in general
 */
@Getter
@Builder
public class VerificationSummary implements Serializable {
    private final boolean validFormat;
    private final String validFormatError;
    private final List<VerificationDetails> details;
}
