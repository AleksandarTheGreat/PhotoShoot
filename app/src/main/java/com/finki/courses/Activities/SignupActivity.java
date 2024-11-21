package com.finki.courses.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finki.courses.R;
import com.finki.courses.Repositories.AuthenticationRepository;
import com.finki.courses.databinding.ActivityLoginBinding;
import com.finki.courses.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends ParentActivity {

    private ActivitySignupBinding binding;
    private AuthenticationRepository authenticationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instantiateObjects();
        additionalThemeChanges();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        addEventListeners();
    }

    @Override
    public void instantiateObjects() {
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authenticationRepository = new AuthenticationRepository(SignupActivity.this);
    }

    @Override
    public void addEventListeners() {
        binding.buttonSignup.setOnClickListener(view -> {
            String email = binding.textInputEditTextEmail.getText().toString().trim();
            String password = binding.textInputEditTextPassword.getText().toString().trim();
            String confirmPassword = binding.textInputEditTextConfirmPassword.getText().toString().trim();

            authenticationRepository.createUser(email, password, confirmPassword, binding);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}