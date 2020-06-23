package vn.candicode.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ExecutionResult implements Serializable {
    private final String language;
    private final String runtimeError;
    private final String timeoutError;
    private final long executionTime; // in millis
    private final String output;

    @Override
    public String toString() {
        return "ExecutionResult{" +
            "language='" + language + '\'' +
            ", runtimeError='" + runtimeError + '\'' +
            ", timeoutError='" + timeoutError + '\'' +
            ", executionTime=" + executionTime +
            ", output='" + output + '\'' +
            '}';
    }
}
