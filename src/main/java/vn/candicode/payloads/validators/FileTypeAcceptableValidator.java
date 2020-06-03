package vn.candicode.payloads.validators;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.filesystem.FileType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class FileTypeAcceptableValidator extends GenericValidator
    implements ConstraintValidator<FileTypeAcceptable, MultipartFile> {
    private List<FileType> acceptableFileTypes;

    @Override
    public void initialize(FileTypeAcceptable constraintAnnotation) {
        this.acceptableFileTypes = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) {
            return NO_NEED_VALIDATE;
        }

        for (FileType fileType : acceptableFileTypes) {
            if (fileType.getContentTypes()
                .stream()
                .anyMatch(contentType -> contentType.equals(file.getContentType()))
            ) {
                return VALID;
            }
        }

        return INVALID;
    }
}
