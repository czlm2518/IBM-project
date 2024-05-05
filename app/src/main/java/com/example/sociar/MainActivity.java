package com.example.sociar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText et_nickname;
    private Button button_quick_start;
    private Button button_start;
    private String nickname;
    private Pattern p;
    private Matcher m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        et_nickname=findViewById(R.id.et_nickname);
        button_quick_start=findViewById(R.id.button_quick_start);
        button_quick_start.setOnClickListener(v -> {
            nickname=et_nickname.getText().toString().trim();

            if(TextUtils.isEmpty(nickname)) {
                Toast.makeText(MainActivity.this, "Please enter the nickname", Toast.LENGTH_SHORT).show();
            }else{
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(nickname);
                if( m.find()){
                    Toast.makeText(MainActivity.this, "Special characters are not allowed", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this, "Successfully Register for Quickstart", Toast.LENGTH_SHORT).show();
                    saveRegisterInfo(nickname);
                    Intent intent = new Intent(MainActivity.this, AR.class);
                    intent.putExtra("info", nickname);
                    startActivity(intent);
                }
            }
        });

        button_start=findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname=et_nickname.getText().toString().trim();

                if(TextUtils.isEmpty(nickname)){
                    Toast.makeText(MainActivity.this, "Please enter the nickname", Toast.LENGTH_SHORT).show();
                }else{
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(nickname);
                    if( m.find()){
                        Toast.makeText(MainActivity.this, "Special characters are not allowed", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Successfully Register for start", Toast.LENGTH_SHORT).show();
                        saveRegisterInfo(nickname);
                        Intent intent = new Intent(MainActivity.this, SettingUp.class);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                    }
                }
            }
        });

        // switch to help page
        Button button_help = findViewById(R.id.button_help);
        button_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void saveRegisterInfo(String nickname) {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", nickname);
        editor.commit();
    }

}
