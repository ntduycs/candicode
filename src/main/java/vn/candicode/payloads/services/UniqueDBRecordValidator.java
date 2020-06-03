package vn.candicode.payloads.services;

public interface UniqueDBRecordValidator {
    boolean isUnique(String column, Object value);

    default String getColumnCannotValidatedMessage(String column) {
        return "Cannot verify the uniqueness of field '" + column + "'. Message: Not supported";
    }
}
