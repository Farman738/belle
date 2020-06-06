package com.e.belle.NetworkConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.e.belle.R;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class InternetService extends Service {
    Timer bindlivedataofpage;
    Activity activity;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
      //  Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return Service.START_STICKY;
    }
//   public InternetService( ActivityFragment activity){
//        this.activity=activity;
//    }
    @Override
    public void onCreate() {
        bindlivedataofpage = new Timer();
        bindlivedataofpage.schedule(new TimerTask() {
            @Override
            public void run() {
                new UserAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 0, 4000);
    }
    //get response apicommunication to bind data
    private class UserAsyncTask extends AsyncTask<URL, Void, String> {
//        PostHttpHandler handler = new PostHttpHandler();              //create connection to url

        @Override
        protected String doInBackground(URL... urls) {
            NetworkUtil networkdetail=new NetworkUtil();
            String Status=networkdetail.getConnectivityStatusString(getApplicationContext());
            return Status;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result=="") {
                return;
            }
            else if(result.equalsIgnoreCase("false")){
              //  NoInterNate();
                Toast.makeText(getApplicationContext(),"Koovs Pfulfil Identifying Internet Connection Issue Please Check", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void NoInterNate() {

        try {
            LayoutInflater inflater = activity.getLayoutInflater();
            View successmessaePopupView = inflater.inflate(R.layout.internet_issue, null);
            final AlertDialog dialog = new AlertDialog.Builder(activity).create();
            dialog.setView(successmessaePopupView);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            dialog.show();
            //You need to add the following line for this solution to work; thanks skayred
            successmessaePopupView.setFocusableInTouchMode(true);
            successmessaePopupView.requestFocus();
            successmessaePopupView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
        }
    }
    ProgressDialog progressDialog;
    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}