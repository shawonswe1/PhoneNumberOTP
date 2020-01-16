package com.example.phonenumberotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText phoneNumber,verificationCode;
    Button letsGo,signIn;
    Spinner spinner;

    private FirebaseAuth mAuth;

    String codeSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.phoneNumber);
        verificationCode = findViewById(R.id.verifyCode);
        spinner = findViewById(R.id.countryCode);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,CountryData.countryNames));

        letsGo = findViewById(R.id.letsGo);
        letsGo.setOnClickListener(this);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.letsGo:
                sendVerificationCode();
                break;
            case R.id.signIn:
                verifySignInCode();
                break;
        }
    }

    private void verifySignInCode() {

        String code = verificationCode.getText().toString().trim();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSend, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Sign In Successfully" , Toast.LENGTH_SHORT).show();

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(getApplicationContext(), "Invalid Code" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {

        String countryCode = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        String number = phoneNumber.getText().toString().trim();

        if (number.isEmpty())
        {
            phoneNumber.setError("Phone Number is required");
            phoneNumber.requestFocus();
            return;
        }
        else if (number.length()<10)
        {
            phoneNumber.setError("Enter a Valid Phone Number");
            phoneNumber.requestFocus();
            return;
        }

        String Number = "+" + countryCode + number;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s , PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s , forceResendingToken);

            codeSend = s;
        }
    };

}
