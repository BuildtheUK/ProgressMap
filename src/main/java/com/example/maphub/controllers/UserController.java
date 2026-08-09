package com.example.maphub.controllers;
import com.example.maphub.entities.DeleteAccountRequest;
import com.example.maphub.entities.PasswordChangeObject;
import com.example.maphub.services.PasswordValidationService;
import com.example.maphub.services.ProxyAPIService;
import com.example.maphub.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    final private ProxyAPIService proxyAPIService;
    final private UserService userService;
    final private PasswordValidationService passwordValidationService;

    public UserController(ProxyAPIService proxyAPIService, UserService userService, PasswordValidationService passwordValidationService) {
        this.proxyAPIService = proxyAPIService;
        this.userService = userService;
        this.passwordValidationService = passwordValidationService;
    }

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
                        "username", proxyAPIService.getUsername(auth.getName())
                )
        );
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest request, Principal principal, HttpSession session) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        // principal.getName() returns the 'uuid' set as the Principal
        String uuid = principal.getName();
        if(userService.login(uuid,request.password) == null){
            return ResponseEntity.badRequest().body("Incorrect password");
        }
        userService.deleteByUuid(uuid);
        session.invalidate();
        return ResponseEntity.ok("Account deleted");
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeObject request, Principal principal){
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }
        String uuid = principal.getName();
        String prevPass = request.previousPassword;
        if(userService.login(uuid,prevPass)== null){
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect current password"));
        }
        List<String> passwordErrors = passwordValidationService.validate(request.newPassword);
        if(!passwordErrors.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("errors", passwordErrors));
        }
        userService.updatePassword(uuid, request.newPassword);
        return ResponseEntity.ok("Password Reset");

    }
}
