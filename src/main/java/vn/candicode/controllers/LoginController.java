package vn.candicode.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.candicode.commons.rest.RestResponse;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.LoginRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.utils.DatetimeUtils;
import vn.candicode.utils.TokenUtils;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class LoginController extends BaseController {
    private final AuthenticationManager authenticationManager;

    private final TokenUtils tokenUtils;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @Override
    protected String getPath() {
        return "auth";
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody @Valid LoginRequest request) {
        Authentication auth = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = tokenUtils.createToken((UserDetails) auth.getPrincipal());
        LocalDateTime tokenExpiration = tokenUtils.getExpirationFromToken(token);

        return ResponseEntity.ok(RestResponse.build(
            Map.of(
                "token", token,
                "type", "Bearer",
                "expiration", tokenExpiration.format(DatetimeUtils.getDatetimeFormatter()),
                "utype", auth.getAuthorities()
            ),
            HttpStatus.OK
        ));
    }

    @GetMapping("/current")
    public ResponseEntity<?> currentUser(@CurrentUser User user) {
        return ResponseEntity.ok(RestResponse.build(user, HttpStatus.OK));
    }
}
