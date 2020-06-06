package com.e.belle.LoginSingUpActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Dashboard;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.Model.AsyModel;
import com.e.belle.Model.LoginModel;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progress;
    private EditText et_login_email, et_login_password;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_email = findViewById(R.id.et_login_email);
        et_login_password = findViewById(R.id.et_login_password);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

//        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());

//        try {
//            String userEmail = sessionManagement.getAddress().email_id;
//            if(userEmail!=null && !sessionManagement.getAddress().email_id.equalsIgnoreCase("")) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//        } catch (Exception e){}

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_button: {

                try {

                    if (et_login_email.getText().toString().equalsIgnoreCase("")) {
                        et_login_email.setError("Please Enter Email Id");
                    } else if (et_login_password.getText().toString().equalsIgnoreCase("")) {
                        et_login_password.setError("Please Enter Password");
                    } else {

                        JSONObject postedJson = new JSONObject();
                        postedJson.put("email_id", et_login_email.getText());
                        postedJson.put("password", et_login_password.getText());
                        postedJson.put("flag", "LOGINSALEAPP");
                        new HitToServer().execute(new AsyModel(AllApiLinks.LoginApi, postedJson, "login"));
                    }

                } catch (Exception e) {

                }
                break;
            }

        }
    }

    private void bindResponse(HttpHandlerModel result, String flagOfAction) {
        try {
            if (result.isConnectStatus()) {
                if (flagOfAction.equalsIgnoreCase("login")) {
                    try {
                       LoginModel loginModel = new Gson().fromJson(result.getJsonResponse(), LoginModel.class);


                        if (loginModel.condition) {
                            SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                            sessionManagement.setUserEmail(loginModel.email_id);
                            sessionManagement.setUserName(loginModel.name);
                            sessionManagement.setUserType(loginModel.user_type);
                            sessionManagement.setRoleId(loginModel.role_id);
                            sessionManagement.setUserId(loginModel.user_id);


                     sessionManagement.setMenu(new Gson().toJson(loginModel.menu));
//                   sessionManagement.setAddress(new Gson().toJson(loginModel.user_details.get(0)));
                            Intent mainIntent = new Intent(LoginActivity.this, Dashboard.class);
                            startActivity(mainIntent);
                            finish();


//                            Toast.makeText(LoginActivity.this, result.getJsonResponse().toString(), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(LoginActivity.this, loginModel.message, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, result.getJsonResponse(), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

        }

    }

    private class HitToServer extends AsyncTask<AsyModel, Void, HttpHandlerModel> {

        private GlobalPostingMethod hitObj = new GlobalPostingMethod();
        private String flagOfAction;

        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Please wait..");
            progress.setCanceledOnTouchOutside(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
        }

        @Override
        protected HttpHandlerModel doInBackground(AsyModel... asyModels) {

            try {
                URL postingUrl = hitObj.createUrl(asyModels[0].getPostingUrl());
                flagOfAction = asyModels[0].getFlagOfAction();

                if (asyModels[0].getPostingJson() == null) {
                    return hitObj.getHttpRequest(postingUrl);
                } else {
                    return hitObj.postHttpRequest(postingUrl, asyModels[0].getPostingJson());
                }
            } catch (Exception e) {
                return hitObj.setReturnMessage(false, "Problem retrieving the user JSON results." + e.getMessage());
            }

        }
        protected void onPostExecute(HttpHandlerModel result) {
            super.onPostExecute(result);
            progress.dismiss();
            bindResponse(result, flagOfAction);
        }
    }
}
