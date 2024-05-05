package com.example.sociar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;


public class SettingUp extends AppCompatActivity{
    private EditText et_occupation;
    private EditText et_professional_interests;
    private EditText pi1;
    private EditText pi2;
    private EditText pi3;
    private EditText pi4;
    private EditText et_hobbies;
    private EditText hb1;
    private EditText hb2;
    private EditText hb3;
    private EditText hb4;
    private EditText et_recent_activities;
    private Button button_add_professional_interests;
    private Button button_add_hobbies;
    private Button button_submit;
    private String occupation;
    private String interests;
    private String interests1;
    private String interests2;
    private String interests3;
    private String interests4;
    private String hobbies;
    private String hobbies1;
    private String hobbies2;
    private String hobbies3;
    private String hobbies4;

    private String activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        et_occupation=findViewById(R.id.et_occupation);
        et_professional_interests=findViewById(R.id.et_professional_interests);
        pi1=findViewById(R.id.et_professional_interests1);
        pi2=findViewById(R.id.et_professional_interests2);
        pi3=findViewById(R.id.et_professional_interests3);
        pi4=findViewById(R.id.et_professional_interests4);
        button_add_professional_interests=findViewById(R.id.button_add_professional_interests);
        et_hobbies=findViewById(R.id.et_hobbies);
        hb1=findViewById(R.id.et_hobbies1);
        hb2=findViewById(R.id.et_hobbies2);
        hb3=findViewById(R.id.et_hobbies3);
        hb4=findViewById(R.id.et_hobbies4);
        button_add_hobbies=findViewById(R.id.button_add_hobbies);
        et_recent_activities=findViewById(R.id.et_recent_activities);
        button_submit=findViewById(R.id.button_submit);


        button_add_professional_interests.setOnClickListener(v -> {
            if (pi3.getVisibility() == View.VISIBLE) {
                pi4.setVisibility(View.VISIBLE);
            }else if (pi2.getVisibility() == View.VISIBLE) {
                pi3.setVisibility(View.VISIBLE);
            }else if (pi1.getVisibility() == View.VISIBLE) {
                pi2.setVisibility(View.VISIBLE);
            }
            pi1.setVisibility(View.VISIBLE);
        });

        button_add_hobbies.setOnClickListener(v -> {
            if (hb3.getVisibility() == View.VISIBLE) {
                hb4.setVisibility(View.VISIBLE);
            }else if (hb2.getVisibility() == View.VISIBLE) {
                hb3.setVisibility(View.VISIBLE);
            }else if (hb1.getVisibility() == View.VISIBLE) {
                hb2.setVisibility(View.VISIBLE);
            }
            hb1.setVisibility(View.VISIBLE);
        });


        button_submit.setOnClickListener(v -> {
            List<String> list = new ArrayList<>();
            Intent haha = getIntent();
            String data1 = haha.getStringExtra("nickname");

            occupation=et_occupation.getText().toString().trim();
            list.add(occupation);
            interests=et_professional_interests.getText().toString().trim();
            list.add(interests);
            interests1=pi1.getText().toString().trim();
            list.add(interests1);
            interests2=pi2.getText().toString().trim();
            list.add(interests2);
            interests3=pi3.getText().toString().trim();
            list.add(interests3);
            interests4=pi4.getText().toString().trim();
            list.add(interests4);
            hobbies=et_hobbies.getText().toString().trim();
            list.add(hobbies);
            hobbies1=hb1.getText().toString().trim();
            list.add(hobbies1);
            hobbies2=hb2.getText().toString().trim();
            list.add(hobbies2);
            hobbies3=hb3.getText().toString().trim();
            list.add(hobbies3);
            hobbies4=hb4.getText().toString().trim();
            list.add(hobbies4);
            activities=et_recent_activities.getText().toString().trim();
            list.add(activities);
            saveRegisterInfo();
            
            String combine = "name:" + data1 + ";";
            int count = list.size();
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    if (list.get(i).length() != 0) {
                        combine += "occupation:" + list.get(i);
                    }
                }
                if (i > 0 && i < 6) {
                    if (list.get(i).length() != 0) {
                        combine += "interests:" + list.get(i);
                    }
                }
                if (i > 5 && i < 11) {
                    if (list.get(i).length() != 0) {
                        combine += "hobbies:" + list.get(i);
                    }
                }
                if (i == 11) {
                    if (list.get(i).length() != 0) {
                        combine += "activities:" + list.get(i);
                    }
                }

                if (list.get(i).length() != 0) {
                    combine += ";";
                }

            }
            Intent intent = new Intent(SettingUp.this,AR.class);
            intent.putExtra("info", combine);
            startActivity(intent);

        });

    }
    private void saveRegisterInfo() {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("occupation", occupation);
        editor.putString("interests", interests);
        editor.putString("interests1", interests1);
        editor.putString("interests2", interests2);
        editor.putString("interests3", interests3);
        editor.putString("interests4", interests4);
        editor.putString("hobbies", hobbies);
        editor.putString("hobbies1", hobbies1);
        editor.putString("hobbies2", hobbies2);
        editor.putString("hobbies3", hobbies3);
        editor.putString("hobbies4", hobbies4);
        editor.putString("activities", activities);
        editor.commit();
    }
}
