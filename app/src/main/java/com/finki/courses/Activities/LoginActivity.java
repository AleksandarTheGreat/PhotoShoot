package com.finki.courses.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.AuthenticationRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.databinding.ActivityLoginBinding;

public class LoginActivity extends ParentActivity {

    private ActivityLoginBinding binding;
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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authenticationRepository = new AuthenticationRepository(LoginActivity.this);
    }

    @Override
    public void addEventListeners() {
        binding.buttonLogin.setOnClickListener(view -> {
            String email = binding.textInputEditTextEmail.getText().toString().trim();
            String password = binding.textInputEditTextPassword.getText().toString().trim();

            authenticationRepository.loginUser(email, password, binding);
        });

        binding.textViewDontHaveAnAccount.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}