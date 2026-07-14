package com.example.maphub;
import com.example.maphub.entities.*;
import com.example.maphub.services.OTCService;
import com.example.maphub.services.PasswordValidationService;
import com.example.maphub.services.ProxyAPIService;
import com.example.maphub.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.synth.SynthTabbedPaneUI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OTCService otcService;
    private final PasswordValidationService passwordValidationService;
    private final ProxyAPIService proxyAPIService;

    public AuthController(UserService service, OTCService otcservice, PasswordValidationService passwordValidationService, ProxyAPIService proxyAPIService) {
        this.userService = service;
        this.otcService = otcservice;
        this.passwordValidationService = passwordValidationService;
        this.proxyAPIService = proxyAPIService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginUser user) {
        String uuid = proxyAPIService.getUuid(user.username);

        if (uuid.isEmpty()){
            return ResponseEntity.badRequest().body("Player hasn't logged onto btuk.org");
        }

        if(userService.userAccountIsActivated(uuid))
        {
            return ResponseEntity
                    .badRequest()
                    .body("Account already exists");
        }
        List<String> passwordErrors = passwordValidationService.validate(user.password);
        if(!passwordErrors.isEmpty()){
            return ResponseEntity.badRequest().body(passwordErrors);
        }
        userService.deleteByUuid(uuid);
        userService.register(uuid,PasswordUtil.hash(user.password));
        otcService.createOneTimeCode(uuid,"REGISTER");//should be a const in the future
        return ResponseEntity.ok("OTC Created");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerificationResponse result, HttpSession session){

        String uuid = proxyAPIService.getUuid(result.username);

        if (otcService.isValidCode(uuid,result.otc))
        {
            userService.verifyUser(uuid);
        }

        createAuthenticatedSession(uuid, result.username,session);

        return ResponseEntity.ok(Map.of("success", true));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginObject request, HttpSession session) {
        String uuid = proxyAPIService.getUuid(request.username);
        User user = userService.login(uuid, request.password);


       if (user == null) {
           return ResponseEntity.status(401).body("Invalid credentials");
       }
       if (!user.isVerified())
       {
           return ResponseEntity.status(401).body("Account not verified");
       }

        createAuthenticatedSession(uuid,request.username,session);
        System.out.println("SESSION ID (login): " + session.getId());
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(Principal principal, HttpSession session) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        // principal.getName() returns the 'uuid' set as the Principal
        String uuid = principal.getName();
        userService.deleteByUuid(uuid);
        logout(session);
        return ResponseEntity.ok("Account deleted");
    }

    private void createAuthenticatedSession(String uuid, String username, HttpSession session) {


        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        uuid,
                        null,
                        java.util.Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
        session.setAttribute("uuid", uuid);
        session.setAttribute("username", username);

    }


}
