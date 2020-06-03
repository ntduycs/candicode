package vn.candicode.payloads.services;

public interface ExistDBRecordValidator {
    boolean isExist(String column, Object value);

    default String getColumnCannotValidatedMessage(String column) {
        return "Cannot verify the existence of field '" + column + "'. Message: Not supported";
    }
}
