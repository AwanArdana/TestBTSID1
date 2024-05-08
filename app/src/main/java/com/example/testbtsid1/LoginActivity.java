package com.example.testbtsid1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
* CREATE BY AWAN
*
* */

public class LoginActivity extends AppCompatActivity {

    Context ctx;

    EditText edt_username;
    EditText edt_password;
    Button btn_login;
    TextView txt_signup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekLogin();
            }
        });

        txt_signup = findViewById(R.id.txt_signup);
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, RegisterActivity.class);
                ctx.startActivity(i);
            }
        });
    }

    void cekLogin(){
        String Username = edt_username.getText().toString();
        String Password = edt_password.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", Username);
            jsonObject.put("password", Password);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String apiUrl = "http://94.74.86.174:8080/api/login";
        String jsonData = jsonObject.toString();


        new Login().execute(apiUrl, jsonData);
//        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
    }

    class Login extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];
            String jsonData = strings[1];

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(jsonData);
                outputStream.flush();
                outputStream.close();

                StringBuilder response = new StringBuilder();
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                result = response.toString();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("login", "onPostExecute: " + s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int statusCode = jsonObject.getInt("statusCode");
                    String message = jsonObject.getString("message");
                    if (statusCode == 2110) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        SettingVariable.token = dataObject.getString("token");
//                    SettingVariable.token = jsonObject.getString("token");
                        Intent i = new Intent(ctx, Home.class);
                        ctx.startActivity(i);
//                    intent
                    }else {
                        Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(ctx, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
