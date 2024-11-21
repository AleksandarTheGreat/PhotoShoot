package com.finki.courses.Repositories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.finki.courses.Activities.LoginActivity;
import com.finki.courses.Activities.MainActivity;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.databinding.ActivityLoginBinding;
import com.finki.courses.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firestore.v1.Document;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AuthenticationRepository implements IAuthenticationRepository{

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Toaster toaster;
    public AuthenticationRepository(Context context){
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();

        this.toaster = new Toaster(context);
    }

    @Override
    public void createUser(String email, String password, String confirmPassword, ActivitySignupBinding binding){
        if (!validateEmail(email)){
            binding.textLayoutEmail.setError("Invalid email address");
            return;
        }

        binding.textLayoutEmail.setError("");
        if (!validatePassword(password)){
            binding.textLayoutPassword.setError("Password is too short");
            return;
        }

        binding.textLayoutPassword.setError("");
        if (!password.equals(confirmPassword)){
            binding.textLayoutConfirmPassword.setError("Passwords do not match");
            return;
        }

        binding.textLayoutConfirmPassword.setError("");
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        toaster.text("Successfully created account");

                        // Here I also create a document for that user
                        FirebaseUser firebaseUser = authResult.getUser();
                        String documentName = firebaseUser.getEmail().toString();
                        firebaseFirestore.collection("Users").document(documentName)
                                .set(new HashMap<>())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Tag", "Created document for user '" + firebaseUser.getEmail() + "' ");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", e.getLocalizedMessage());
                                    }
                                });

                        Intent intentGoToMainActivity = new Intent(context, MainActivity.class);
                        intentGoToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intentGoToMainActivity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void loginUser(String email, String password, ActivityLoginBinding binding) {
        if (!validateEmail(email)){
            binding.textLayoutEmail.setError("Invalid email address");
            return;
        }

        binding.textLayoutEmail.setError("");
        if (!validatePassword(password)){
            binding.textLayoutPassword.setError("Password is too short");
            return;
        }

        binding.textLayoutPassword.setError("");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        toaster.text("Welcome back, sir '" + user.getEmail()  + "'");

                        Intent intentGoToMainActivity = new Intent(context, MainActivity.class);
                        intentGoToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intentGoToMainActivity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getMessage());
                    }
                });
    }

    @Override
    public void signOutUser() {
        firebaseAuth.signOut();

        Intent goToLoginActivity = new Intent(context, LoginActivity.class);
        goToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(goToLoginActivity);

        toaster.text("Signing out");
    }

    @Override
    public boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean validatePassword(String password) {
        return password.length() >= 8;
    }


}
