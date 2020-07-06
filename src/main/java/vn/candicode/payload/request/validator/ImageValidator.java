package vn.candicode.payload.request.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) {
            return true; // No need to validate if no file was chosen
        }

        final String contentType = file.getContentType();

        assert contentType != null;

        return isSupportedFileType(contentType);
    }

    private boolean isSupportedFileType(String contentType) {
        return contentType.equals("image/png")
            || contentType.equals("image/jpg")
            || contentType.equals("image/jpeg");
    }
}
