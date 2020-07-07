package vn.candicode.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;

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

    public static VerificationDetails compileFailed(CompileResult compileResult) {
        return VerificationDetails.builder()
            .compiled(false)
            .compileError(compileResult.getCompileError())
            .language(compileResult.getLanguage())
            .executionTime(0)
            .timoutError(null)
            .runtimeError(null)
            .output(null)
            .build();
    }

    public static VerificationDetails executeCompleted(ExecutionResult executionResult) {
        return VerificationDetails.builder()
            .compiled(true)
            .compileError(null)
            .language(executionResult.getLanguage())
            .executionTime(executionResult.getExecutionTime())
            .timoutError(executionResult.getTimeoutError())
            .runtimeError(executionResult.getRuntimeError())
            .output(executionResult.getOutput())
            .build();
    }
}
