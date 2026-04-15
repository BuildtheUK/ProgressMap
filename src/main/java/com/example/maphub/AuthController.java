package com.example.maphub;
import com.example.maphub.entities.*;
import com.example.maphub.services.OTCService;
import com.example.maphub.services.PasswordValidationService;
import com.example.maphub.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OTCService otcService;
    private final PasswordValidationService passwordValidationService;

    public AuthController(UserService service, OTCService otcservice, PasswordValidationService passwordValidationService) {
        this.userService = service;
        this.otcService = otcservice;
        this.passwordValidationService = passwordValidationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(userService.userExists(user.getUsername()))
        {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }
        List<String> passwordErrors = passwordValidationService.validate(user.getPassword());
        if(!passwordErrors.isEmpty()){
            return ResponseEntity.badRequest().body(passwordErrors);
        }
        otcService.createOneTimeCode(user.getUsername(), user.getPassword());
        return ResponseEntity.ok("OTC Created");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerificationResponse result, HttpSession session){
        VerificationResult r = otcService.fetchUserDetails(result.username,result.otc);
        User user = userService.register(r);
        session.setAttribute("user", user.getUsername());
        return ResponseEntity.ok("Logged in");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginObject request, HttpSession session) {
        User user = userService.login(request.username, request.password);

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        session.setAttribute("user", user.getUsername());
        System.out.println("SESSION ID (login): " + session.getId());
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }


}
