package vn.candicode.common;

public enum FileStorageType {
    CHALLENGE(1),
    SUBMISSION(2),
    TUTORIAL(3),
    CONTEST(4),
    AVATAR(5),
    BANNER(6),
    ;

    public final int code;

    FileStorageType(int code) {
        this.code = code;
    }
}
