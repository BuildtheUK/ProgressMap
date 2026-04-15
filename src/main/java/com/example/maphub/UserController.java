package com.example.maphub;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {

        if (session.getAttribute("user") == null){
            return ResponseEntity.status(401).body("Not logged in");
        }
        String username = session.getAttribute("user").toString();

        if (username == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        return ResponseEntity.ok(username);
    }
}
