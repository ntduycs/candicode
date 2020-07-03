package vn.candicode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.candicode.security.SecurityEntryPoint;
import vn.candicode.security.SecurityTokenFilter;
import vn.candicode.security.UserPrincipalService;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final SecurityEntryPoint entryPoint;
    private final SecurityTokenFilter tokenFilter;
    private final UserPrincipalService userPrincipalService;

    public SecurityConfig(SecurityEntryPoint entryPoint, SecurityTokenFilter tokenFilter, UserPrincipalService userPrincipalService) {
        this.entryPoint = entryPoint;
        this.tokenFilter = tokenFilter;
        this.userPrincipalService = userPrincipalService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userPrincipalService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(entryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers(POST, "/auth/login").anonymous()

            .antMatchers(POST, "/students").anonymous()
            .antMatchers(PUT, "/students/*/roles").hasAnyAuthority("admin")

            .antMatchers(POST, "/admins").hasAnyAuthority("super admin", "manage admin")
            .antMatchers(PUT, "/admins/*/roles").hasAnyAuthority("super admin", "manage admin")

            .antMatchers(POST, "/challenges", "/challenges/*").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(DELETE, "/challenges/*").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(GET, "/challenges", "/challenges/*").permitAll()

            .antMatchers(GET, "/challenges/*/comments").permitAll()

            .antMatchers(POST, "/challenges/*/languages").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(DELETE, "/challenges/*/languages").hasAnyAuthority("challenge creator", "admin")

            .antMatchers(POST, "/challenges/*/submissions").hasAuthority("student")
            .antMatchers(GET, "/challenges/*/submissions").permitAll()

            .antMatchers(POST, "/challenges/*/testcases").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(DELETE, "/challenges/*/testcases").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(PUT, "/challenges/*/testcases").hasAnyAuthority("challenge creator", "admin")
            .antMatchers(POST, "/challenges/*/testcases/verification").hasAnyAuthority("challenge creator", "admin")

            .antMatchers(POST, "/tutorials", "/tutorials/*").hasAnyAuthority("tutorial creator", "admin")
            .antMatchers(DELETE, "/tutorials/*").hasAnyAuthority("tutorial creator", "admin")
            .antMatchers(GET, "/tutorials", "tutorials/*").permitAll()

            .antMatchers(GET, "/tutorials/*/comments").permitAll()

            .antMatchers(POST, "/contests", "/contests/*").hasAnyAuthority("contest creator", "admin")
            .antMatchers(DELETE, "/contests/*").hasAnyAuthority("contest creator", "admin")
            .antMatchers(GET, "/contests", "contests/*").permitAll()

            .antMatchers(POST, "/contests/*/rounds").hasAnyAuthority("contest creator", "admin")
            .antMatchers(DELETE, "/contests/*/rounds").hasAnyAuthority("contest creator", "admin")
            .antMatchers(PUT, "/contests/*/rounds").hasAnyAuthority("contest creator", "admin")

            .antMatchers(POST, "/contests/*/registration").hasAuthority("student")

            .anyRequest().authenticated();

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
