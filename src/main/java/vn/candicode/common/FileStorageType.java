package vn.candicode.common;

public enum FileStorageType {
    CHALLENGE(1),
    SUBMISSION(2),
    AVATAR(3),
    BANNER(4),
    STAGING(5);

    public final int code;

    FileStorageType(int code) {
        this.code = code;
    }
}
