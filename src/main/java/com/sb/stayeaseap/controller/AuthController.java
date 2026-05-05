package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.service.AuthService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model){
            if (error != null) model.addAttribute("loginError", "Invalid email or password.");
            if (logout != null) model.addAttribute("logoutMessage", "You have been logged out.");
            return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam("confirm_password") String confirmPassword,
                                 Model model) {
        try {
            authService.register(name, email, password, confirmPassword);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerError", e.getMessage());
            return "auth/register";
        }
    }
    
}
