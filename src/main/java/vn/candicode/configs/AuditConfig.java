package vn.candicode.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import vn.candicode.models.User;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {
    @Bean
    public AuditorAware<Long> auditorAware() {
        return new AuditorAwareImpl();
    }

    private static class AuditorAwareImpl implements AuditorAware<Long> {

        @Override
        @NonNull
        public Optional<Long> getCurrentAuditor() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return principal instanceof UserDetails ? Optional.of(((User) principal).getId()) : Optional.empty();
        }
    }
}
