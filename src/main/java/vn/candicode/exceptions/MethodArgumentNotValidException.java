package vn.candicode.exceptions;

import java.util.Map;

public class MethodArgumentNotValidException extends RuntimeException {
    private Map<String, Object> bindingResult;

    public Map<String, Object> getBindingResult() {
        return bindingResult;
    }

    public MethodArgumentNotValidException(Map<String, Object> bindingResult) {
        this.bindingResult = bindingResult;
    }
}
