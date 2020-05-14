package vn.candicode.payloads.validators;

public interface UniqueValidatorService {
    boolean isUnique(String key, Object value);

    static String getUnsupportedFieldMessage(String key) {
        return "Cannot verify the uniqueness of field '" + key + "'. Message: Not supported";
    }
}
