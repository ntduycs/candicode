package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.LoginRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.security.WebTokenProvider;
import vn.candicode.utils.DatetimeUtils;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Log4j2
public class LoginController extends GenericController {
    private final AuthenticationManager authenticationManager;
    private final WebTokenProvider tokenProvider;

    public LoginController(AuthenticationManager authenticationManager, WebTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginRequest body) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword()));

        final String token = tokenProvider.generateWebToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(GenericResponse.from(
            Map.of(
                "token", token,
                "tokenType", tokenProvider.getTokenType(),
                "expiration", tokenProvider.getExpiration(token).format(DatetimeUtils.DEFAULT_DATETIME_FORMAT),
                "roles", authentication.getAuthorities()
            )
        ));
    }

    @GetMapping("/auth/current")
    public ResponseEntity<?> currentUser(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(GenericResponse.from(currentUser));
    }

    @Override
    protected String getResourceBasePath() {
        return "auth";
    }
}
