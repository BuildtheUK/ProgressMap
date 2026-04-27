package com.example.maphub;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {

        if (auth == null ||
                auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return ResponseEntity.status(401)
                    .body(Map.of("loggedIn", false));
        }

        return ResponseEntity.ok(
                Map.of(
                        "loggedIn", true,
                        "username", auth.getName()
                )
        );
    }
}
