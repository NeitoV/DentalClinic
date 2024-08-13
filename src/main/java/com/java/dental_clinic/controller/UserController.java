package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.ChangePasswordDTO;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/mail/send")
    public ResponseEntity<?> sendMailActive(@RequestBody Map<String, String> mailConfirm) throws MessagingException{
        String email = mailConfirm.get("email");


        return ResponseEntity.ok(userService.sendMailActiveUser(email));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/mail/active")
    public ResponseEntity<?> activeEmail(@RequestBody Map<String, String> mailConfirm) {
        String email = mailConfirm.get("email");
        String otp = mailConfirm.get("otp");

        return ResponseEntity.ok(userService.activeEmail(email, otp));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {

        return ResponseEntity.ok(userService.changePassword(changePasswordDTO));
    }


    @PostMapping("/mail/forget-password")
    ResponseEntity<?> sendMailForgetPassword(@RequestBody Map<String, String> mailConfirm) throws MessagingException {
        String email = mailConfirm.get("email");

        return new ResponseEntity<>(userService.sendMailForgetPassword(email), HttpStatus.CREATED);
    }

    @PostMapping("/mail/otp")
    ResponseEntity<?> checkOTP(@RequestBody Map<String, String> mailConfirm) {
        String email = mailConfirm.get("email");
        String otp = mailConfirm.get("otp");

        return ResponseEntity.ok(userService.checkOTPForgetPassword(otp, email));
    }

    @PutMapping("/password")
    ResponseEntity<?> changePassForget(@RequestBody Map<String, String> changePassForget) {
        String email = changePassForget.get("email");
        String pass = changePassForget.get("newPassword");

        return ResponseEntity.ok(userService.changePasswordForget(pass, email));
    }
}
