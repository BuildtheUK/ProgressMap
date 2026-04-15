package com.example.maphub.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordValidationService {

    public List<String> validate(String password) {

        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
            return errors;
        }

        if (password.length() < 10) {
            errors.add("Password length must be longer than 9");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain an Uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain a Lowercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            errors.add("Password must contain a number");
        }

        if (!password.matches(".*[$!@#%^&*].*")) {
            errors.add("Password must contain one of $!@#%^&*");
        }

        if (!password.matches("[A-Za-z0-9$!@#%^&*]+")) {
            errors.add("Password contains an illegal character");
        }

        return errors;
    }
}
