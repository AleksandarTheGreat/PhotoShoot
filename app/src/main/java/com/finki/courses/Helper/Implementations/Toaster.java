package com.finki.courses.Helper.Implementations;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finki.courses.Helper.IToaster;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Toaster implements IToaster {
    private Context context;
    private AppCompatActivity appCompatActivity;

    public Toaster(Context context) {
        this.context = context;
    }

    public Toaster(Context context, AppCompatActivity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }


    @Override
    public void text(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void alert(String text) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Alert")
                .setMessage(text)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    @Override
    public void alert(String title, String text) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
}
