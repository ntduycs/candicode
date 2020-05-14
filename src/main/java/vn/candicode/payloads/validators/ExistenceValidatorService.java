package vn.candicode.payloads.validators;

public interface ExistenceValidatorService {
    boolean exists(String key, Object value);

    static String getUnsupportedFieldMessage(String key) {
        return "Cannot verify the existence of field '" + key + "'. Message: Not supported";
    }
}
