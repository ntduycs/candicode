package vn.candicode.payload.request.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ZipValidator implements ConstraintValidator<Zip, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) {
            return true; // No need to validate if no file was chosen
        }

        if (file.isEmpty()) {
            return false;
        }

        final String contentType = file.getContentType();

        assert contentType != null;

        return isSupportedFileType(contentType);
    }

    private boolean isSupportedFileType(String contentType) {
        // application/zip, application/octet-stream, application/x-zip-compressed, multipart/x-zip
        return contentType.equals("application/zip")
            || contentType.equals("application/octet-stream")
            || contentType.equals("application/x-zip-compressed")
            || contentType.equals("multipart/x-zip");
    }
}
