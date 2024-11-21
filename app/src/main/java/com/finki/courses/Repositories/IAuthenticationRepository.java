package com.finki.courses.Repositories;

import com.finki.courses.databinding.ActivityLoginBinding;
import com.finki.courses.databinding.ActivitySignupBinding;

public interface IAuthenticationRepository {

    void createUser(String email, String password, String confirmPassword, ActivitySignupBinding binding);
    void loginUser(String email, String password, ActivityLoginBinding binding);
    void signOutUser();

    boolean validateEmail(String email);
    boolean validatePassword(String password);
}
