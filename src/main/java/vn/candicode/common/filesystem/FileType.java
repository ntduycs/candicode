package vn.candicode.common.filesystem;

import java.util.List;

public enum FileType {
    ZIP(List.of(
        "application/zip",
        "application/octet-stream",
        "application/x-zip-compressed",
        "multipart/x-zip"
    )),
    RAR(List.of(
        "application/x-rar-compressed",
        "application/vnd.rar"
    ));

    private final List<String> contentTypes;

    FileType(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }
}
