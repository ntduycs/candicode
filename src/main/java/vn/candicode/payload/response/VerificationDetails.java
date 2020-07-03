package vn.candicode.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@Builder
public class VerificationDetails implements Serializable {
    private final String language;

    private final boolean compiled;
    private final String compileError;

    private final String output;
    private final String runtimeError;
    private final String timoutError;
    private final long executionTime;
}
