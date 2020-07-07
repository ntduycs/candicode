package vn.candicode.payload.response;

import lombok.Builder;
import lombok.Getter;
import vn.candicode.util.RegexUtils;

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

    public static VerificationSummary invalidTestcaseFormat(String validFormat) {
        return VerificationSummary.builder()
            .validFormat(false)
            .validFormatError("Input should be " + RegexUtils.resolveRegex(validFormat))
            .details(null)
            .build();
    }
}
