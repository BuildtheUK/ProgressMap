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

    @PostMapping("/newOTC")
    public ResponseEntity<?> newOTC(@RequestBody NewOTCRequest request){
        String uuid = proxyAPIService.getUuid(request.username);
        if (uuid.isEmpty() || !userService.userExists(uuid)){
            return ResponseEntity.badRequest().body("Invalid username");

        }
        otcService.createOneTimeCode(uuid,request.purpose);
        return ResponseEntity.ok().body("New OTC created");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword( @RequestBody OTCResetPasswordRequest request) {
        String uuid = proxyAPIService.getUuid(request.username);
        if (uuid.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user");
        }
        if (!otcService.isValidCode(uuid, request.otc, "RESETPASSWORD")) {
            return ResponseEntity.badRequest().body("Invalid or expired code");
        }

        List<String> passwordErrors = passwordValidationService.validate(request.newPassword);
        if (!passwordErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", passwordErrors));
        }
        userService.updatePassword(uuid, request.newPassword);

        return ResponseEntity.ok("Password has been reset successfully. Please log in.");
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

        if (!otcService.isValidCode(uuid,result.otc,"REGISTER"))
        {
            return ResponseEntity.badRequest().body("Invalid or expired code");
        }
        userService.verifyUser(uuid);
        createAuthenticatedSession(uuid,session);

        return ResponseEntity.ok(Map.of("success", true));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginObject request, HttpSession session) {
        String uuid = proxyAPIService.getUuid(request.username);
        User user = userService.login(uuid, request.password);

        return logInUser(user,session);

    }

    public ResponseEntity<?> logInUser(User user, HttpSession session){
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        if (!user.isVerified())
        {
            return ResponseEntity.status(401).body("Account not verified");
        }

        createAuthenticatedSession(user.getUuid(),session);
        System.out.println("SESSION ID (login): " + session.getId());
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }



    private void createAuthenticatedSession(String uuid, HttpSession session) {


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

    }


}
