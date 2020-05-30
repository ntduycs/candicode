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

@Component("challenge")
public class ChallengeRequestValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> cls) {
        return ChallengeRequest.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        ChallengeRequest request = (ChallengeRequest) o;

        if (!StringUtils.hasText(request.getTitle())) {
            errors.rejectValue("title", "challenge.field.missing", new String[]{"title"}, "Required field is missing");
        }

        if (!StringUtils.hasText(request.getLevel())) {
            errors.rejectValue("level", "challenge.field.missing", new String[]{"level"}, "Required field is missing");
        } else {
            try {
                ChallengeLevel.valueOf(request.getLevel().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.rejectValue("level", "challenge.enum.not-found", new String[]{ChallengeLevel.class.getSimpleName()}, "Given value not belong the enum");
            }
        }

        if (!StringUtils.hasText(request.getDescription())) {
            errors.rejectValue("description", "challenge.field.missing", new String[]{"description"}, "Required field is missing");
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            if (!List.of("image/jpeg", "image/png").contains(request.getBanner().getContentType())) {
                errors.rejectValue("banner", "challenge.mime-type.not-supported", new String[]{request.getBanner().getContentType()}, "Mime-type is not supported");
            }
        }

        if (!StringUtils.hasText(request.getLanguage())) {
            errors.rejectValue("language", "challenge.field.missing", new String[]{"language"}, "Required field is missing");
        } else {
            try {
                ChallengeLanguage.valueOf(request.getLanguage().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.rejectValue("language", "challenge.enum.not-found", new String[]{ChallengeLanguage.class.getSimpleName()}, "Given value not belong the enum");
            }
        }

        if (!StringUtils.hasText(request.getRunPath())) {
            errors.rejectValue("runPath", "challenge.field.missing", new String[]{"runPath"}, "Required field is missing");
        }

        if (!StringUtils.hasText(request.getCompilePath())) {
            errors.rejectValue("compilePath", "challenge.field.missing", new String[]{"compilePath"}, "Required field is missing");
        }

        if (!StringUtils.hasText(request.getTcInputFormat())) {
            errors.rejectValue("tcInputFormat", "challenge.field.missing", new String[]{"tcInputFormat"}, "Required field is missing");
        } else {
            try {
                Pattern.compile(request.getTcInputFormat());
            } catch (PatternSyntaxException e) {
                errors.rejectValue("tcInputFormat", "challenge.regex.invalid");
            }
        }

        if (!StringUtils.hasText(request.getTcOutputFormat())) {
            errors.rejectValue("tcOutputFormat", "challenge.field.missing", new String[]{"tcOutputFormat"}, "Required field is missing");
        } else {
            try {
                Pattern.compile(request.getTcOutputFormat());
            } catch (PatternSyntaxException e) {
                errors.rejectValue("tcOutputFormat", "challenge.regex.invalid");
            }
        }
    }
}
