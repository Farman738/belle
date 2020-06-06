package com.e.belle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.belle.LoginSingUpActivity.LoginActivity;
import com.e.belle.SessionManagment.SessionManagement;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreenActivity extends AppCompatActivity {

    public TextView tv_company,tv_poweredby,tv_pristine;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SessionManagement sessionManagement;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGB_888);
    }
    Thread splashThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setAnimations();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkPermission()) {
//                    Toast.makeText(LogoPopupActivity.this, "Permission already granted.", Toast.LENGTH_LONG).show();
                    gotoLogin();
                } else {
//                    Toast.makeText(LogoPopupActivity.this, "Please request permission.", Toast.LENGTH_LONG).show();
                    requestPermission();
                }
            }
        }, 2000);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    void gotoLogin() {
        try {

            sessionManagement = new SessionManagement(getApplicationContext());

            if (!sessionManagement.getUserEmail().equalsIgnoreCase("")) {

                Intent intent = new Intent(SplashScreenActivity.this, Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {

                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }catch (Exception e){
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        SplashScreenActivity.this.finish();
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    //todo when request permision hit
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && writeAccepted && cameraAccepted) {
//                        Toast.makeText(LogoPopupActivity.this, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
                        gotoLogin();
                    } else {
//                        Toast.makeText(LogoPopupActivity.this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    //todo show message yes or no after disallow
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreenActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void setAnimations() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();

        LinearLayout linearLayout = findViewById(R.id.tv_welcome);
        linearLayout.clearAnimation();
        linearLayout.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,R.anim.translate_up_from_bottom);
        animation.reset();

//        tv_company = findViewById(R.id.tv_company);
        tv_poweredby = findViewById(R.id.tv_poweredby);
        tv_pristine = findViewById(R.id.tv_pristine);
        ImageView company_splash_logo = findViewById(R.id.company_splash_logo);
        company_splash_logo.clearAnimation();
        company_splash_logo.startAnimation(animation);
//        tv_company.startAnimation(animation);
        tv_poweredby.startAnimation(animation);
        tv_pristine.startAnimation(animation);

        splashThread = new Thread(){

            public void run() {
                try {
                    int waited = 0;
                    while (waited<2000) {
                        sleep(100);
                        waited+= 100;
                    }

                } catch (InterruptedException e){}
            }
        }; splashThread.start();
    }
}
