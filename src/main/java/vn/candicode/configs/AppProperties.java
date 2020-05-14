package vn.candicode.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "candicode")
@Validated
@Getter
@Setter
public class AppProperties {
    @NotNull
    @Size(min = 1)
    private Map<String, String> jwt;

    @NotNull
    @Size(min = 1)
    private Map<String, String> auth;

    @NotNull
    @Size(min = 1)
    private Map<String, String> throttle;

    @NotNull
    @Size(min = 1)
    private Map<String, String> storage;
}
