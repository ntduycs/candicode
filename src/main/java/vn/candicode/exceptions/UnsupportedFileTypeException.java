package vn.candicode.exceptions;

import com.google.common.collect.Iterables;
import lombok.Getter;

import java.util.List;

@Getter
public class UnsupportedFileTypeException extends RuntimeException {
    private final String rejectedFileType;
    private final Iterable<String> supportedFileTypes;

    @SafeVarargs
    public UnsupportedFileTypeException(String rejectedFileType, List<String>... supportedFileTypes) {
        super(String.format("File with MIME type '%s' not supported", rejectedFileType));
        this.rejectedFileType = rejectedFileType;
        this.supportedFileTypes = Iterables.unmodifiableIterable(Iterables.concat(supportedFileTypes));
    }
}
