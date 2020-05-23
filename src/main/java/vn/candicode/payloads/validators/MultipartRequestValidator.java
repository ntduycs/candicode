package vn.candicode.payloads.validators;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.requests.ChallengeRequest;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class MultipartRequestValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> cls) {
        return ChallengeRequest.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        ChallengeRequest request = (ChallengeRequest) o;

        if (!StringUtils.hasText(request.getTitle())) {
            errors.rejectValue("title", "title", new String[]{"title"}, "challenge.field.missing");
        }

        if (!StringUtils.hasText(request.getLevel())) {
            errors.rejectValue("level", "level", new String[]{"level"}, "challenge.field.missing");
        } else {
            try {
                ChallengeLevel.valueOf(request.getLevel().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.rejectValue("level", "level", new String[]{ChallengeLevel.class.getSimpleName()}, "challenge.enum.not-found");
            }
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            if (!List.of("image/jpeg", "image/png").contains(request.getBanner().getContentType())) {
                errors.rejectValue("banner", "banner", new String[] {request.getBanner().getContentType()}, "challenge.mime-type.not-supported");
            }
        }

        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            errors.rejectValue("description", "challenge.file.missing");
        } else if (!List.of("text/markdown").contains(request.getDescription().getContentType())) {
            errors.rejectValue("description", "description", new String[] {request.getDescription().getContentType()}, "challenge.mime-type.not-supported");
        }

        if (!StringUtils.hasText(request.getLanguage())) {
            errors.rejectValue("language", "language", new String[]{"language"}, "challenge.field.missing");
        } else {
            try {
                ChallengeLanguage.valueOf(request.getLanguage().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.rejectValue("language", "language", new String[]{ChallengeLanguage.class.getSimpleName()}, "challenge.enum.not-found");
            }
        }

        if (!StringUtils.hasText(request.getTargetPath())) {
            errors.rejectValue("targetPath", "targetPath", new String[]{"targetPath"}, "challenge.field.missing");
        }

        if (!StringUtils.hasText(request.getBuildPath())) {
            errors.rejectValue("buildPath", "buildPath", new String[]{"buildPath"}, "challenge.field.missing");
        }

        if (!StringUtils.hasText(request.getTcInputFormat())) {
            errors.rejectValue("tcInputFormat", "tcInputFormat", new String[]{"tcInputFormat"}, "challenge.field.missing");
        } else {
            try {
                Pattern.compile(request.getTcInputFormat());
            } catch (PatternSyntaxException e) {
                errors.rejectValue("tcInputFormat", "challenge.regex.invalid");
            }
        }

        if (!StringUtils.hasText(request.getTcOutputFormat())) {
            errors.rejectValue("tcOutputFormat", "tcOutputFormat", new String[]{"tcOutputFormat"}, "challenge.field.missing");
        } else {
            try {
                Pattern.compile(request.getTcOutputFormat());
            } catch (PatternSyntaxException e) {
                errors.rejectValue("tcOutputFormat", "challenge.regex.invalid");
            }
        }
    }
}
