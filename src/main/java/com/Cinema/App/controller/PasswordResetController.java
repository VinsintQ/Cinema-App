package com.Cinema.App.controller;

import com.Cinema.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth/users")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @GetMapping("/reset-password")
    public ModelAndView showResetPasswordPage(@RequestParam String token) {
        ModelAndView mav = new ModelAndView("reset-password-form");
        mav.addObject("token", token);
        return mav;
    }

    @PostMapping("/resetPassword")
    public ModelAndView passwordResetActivator(@RequestParam String token, @RequestParam String password) {
        ModelAndView mav = new ModelAndView("reset-password-form");
        mav.addObject("token", token);
        try {
            userService.resetPasswordActivator(token, password);
            mav.addObject("success", "Your password has been reset successfully. You can now log in.");
        } catch (RuntimeException e) {
            mav.addObject("error", "Invalid or expired reset link. Please request a new one.");
        }
        return mav;
    }
}
