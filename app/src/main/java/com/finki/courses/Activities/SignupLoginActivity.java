package com.finki.courses.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finki.courses.R;
import com.finki.courses.databinding.ActivitySignupLoginBinding;

public class SignupLoginActivity extends ParentActivity {

    private ActivitySignupLoginBinding binding;

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
        binding = ActivitySignupLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void addEventListeners() {
        binding.buttonLoginSignup.setOnClickListener(view -> {
            Intent intentGoToMainActivity = new Intent(SignupLoginActivity.this, MainActivity.class);
            startActivity(intentGoToMainActivity);
            finish();
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}