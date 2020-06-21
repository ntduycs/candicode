package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.LoginRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.SecurityTokenProvider;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.DatetimeUtils;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthController extends Controller {
    private final AuthenticationManager authenticationManager;
    private final SecurityTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, SecurityTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected String getPath() {
        return "auth";
    }

    @PostMapping(path = "auth/login", produces = {"application/json"})
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginRequest payload) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(payload.getEmail(), payload.getPassword()));

        final String token = tokenProvider.generateWebToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(ResponseFactory.build(
            Map.of(
                "token", token,
                "tokenType", tokenProvider.getTokenType(),
                "expiration", tokenProvider.getExpiration(token).format(DatetimeUtils.JSON_DATETIME_FORMAT),
                "roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
            )
        ));
    }

    @GetMapping(path = "auth/current", produces = {"application/json"})
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(ResponseFactory.build(principal));
    }
}
