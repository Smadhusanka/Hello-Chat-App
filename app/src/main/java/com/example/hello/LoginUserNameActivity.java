package com.example.hello;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello.model.UserModel;
import com.example.hello.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUserNameActivity extends AppCompatActivity {

    EditText userNameInput;
    Button loginBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assign id's for variables
        userNameInput = findViewById(R.id.loginUserName);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.loginProgressBar);

        //catch value passing from LoginOtpActivity
        phoneNumber = getIntent().getExtras().getString("phone");
        getUserName();

        //login button
        loginBtn.setOnClickListener((v -> {
            setUserName();
        }));
    }

    //set username function
    void setUserName(){

        String username = userNameInput.getText().toString();
        if(username.isEmpty() || username.length()<3){
            userNameInput.setError("UserName should be at least 3 characters");
            return;
        }

        setInProgress(true);
        //check user already registered or not
        if(userModel!=null){
            userModel.setUsername(username);
        }else {
            userModel = new UserModel(phoneNumber,username, Timestamp.now(), FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUserNameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });
    }

    //get current user details from firebase
    void getUserName(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel= task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        userNameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    //handle progress bar and login button
    void setInProgress(boolean inProgress){
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

}