package com.e.belle.ui.upgradeApp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.Model.AndroidVersion;
import com.e.belle.Model.AsyModel;
import com.e.belle.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UpgradeAppFragment extends Fragment {

  boolean downloadData = false;
  private UpgradeAppViewModel mViewModel;
  private ProgressDialog progress;
  private LinearLayout newUpdateSection;
  private TextView versioncode_versionName, new_versioncode_versionName, new_Message, errorCode;
  private Button downloadButton;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mViewModel = ViewModelProviders.of(this).get(UpgradeAppViewModel.class);
    View root = inflater.inflate(R.layout.upgrade_app_fragment, container, false);
    inItData(root);
    return root;
  }

  void inItData(View root) {
    //todo init state
    newUpdateSection = root.findViewById(R.id.newUpdateSection);
    versioncode_versionName = root.findViewById(R.id.versioncode_versionName);
    new_versioncode_versionName = root.findViewById(R.id.new_versioncode_versionName);
    new_Message = root.findViewById(R.id.new_Message);
    downloadButton = root.findViewById(R.id.downloadButton);
    errorCode = root.findViewById(R.id.errorCode);
//        loadingDialog = new LoadingDialog();

    mViewModel.getAndroidVersion().observe(getActivity(), androidVersion -> {
      //todo code
      try {
        int versioncode = getVersionCode(getActivity().getApplicationContext());
        float versionName = getVersionName(getActivity().getApplicationContext());
        versioncode_versionName.setText(versioncode + "." + getVersionName(getActivity().getApplicationContext()));
        if (versioncode < androidVersion.id || versionName < androidVersion.version) {
          newUpdateSection.setVisibility(View.VISIBLE);
          new_versioncode_versionName.setText(androidVersion.id + "." + androidVersion.version);
          new_Message.setText(androidVersion.avl2.get(0).change_log);
        } else {
          newUpdateSection.setVisibility(View.GONE);
        }
      } catch (Exception e) {

      }
    });
    //todo get new Version of code
    new GetLatestVerisionData().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new AsyModel(AllApiLinks.versioncodeapiurl, null, "getApkVersion"));
    downloadButton.setOnClickListener(view -> {
      try {
        if (downloadData == false) {
          downloadData = true;
          errorCode.setText("");
          Animation myAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.press_btton);
          downloadButton.startAnimation(myAnim);
          new DownloadNewVersion().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, AllApiLinks.globalurl + mViewModel.getAndroidVersion().getValue().url);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle("Update App");
  }

  private void UpdateCurrentVersion(String result) {
    try {
      ArrayList<AndroidVersion> tempmodel = new Gson().fromJson(result, new TypeToken<List<AndroidVersion>>() {
      }.getType());
      mViewModel.setAndroidVersion(tempmodel.get(0));
    } catch (Exception ee) {
    } finally {
      progress.dismiss();
    }
  }

  int getVersionCode(Context context) throws Exception {
    PackageManager pm = context.getPackageManager();
    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
    return pi.versionCode;
  }

  float getVersionName(Context context) throws Exception {
    PackageManager pm = context.getPackageManager();
    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
    return Float.parseFloat(pi.versionName);
  }

  void OpenNewVersion() {
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + "Pristine.apk")),
              "application/vnd.android.package-archive");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    } catch (Exception e) {
      File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + "Pristine.apk");
      Intent install = new Intent(Intent.ACTION_VIEW);
      install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
// Old Approach
      install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
// End Old approach
// New Approach
      Uri apkURI = FileProvider.getUriForFile(
              getActivity(),
              getActivity().getApplicationContext()
                      .getPackageName() + ".provider", file);
      install.setDataAndType(apkURI, "application/vnd.android.package-archive");
      install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
// End New Approach
      startActivity(install);
    }
  }

  private class GetLatestVerisionData extends AsyncTask<AsyModel, Void, HttpHandlerModel> {
    private GlobalPostingMethod hitObj = new GlobalPostingMethod();
    private String flagOfAction;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = new ProgressDialog(getActivity());
      progress.setMessage("Please Wait..");
      progress.setCanceledOnTouchOutside(false);
      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progress.show();
    }

    @Override
    protected HttpHandlerModel doInBackground(AsyModel... asyModels) {
      try {
        URL url = hitObj.createUrl(AllApiLinks.versioncodeapiurl);
        if (asyModels[0].getPostingJson() == null)
          return hitObj.getHttpRequest(url);
        else
          return hitObj.postHttpRequest(url, asyModels[0].getPostingJson());
      } catch (Exception e) {
        return hitObj.setReturnMessage(false, "Problem retrieving the user JSON results." + e.getMessage());
      }
    }

    @Override
    protected void onPostExecute(HttpHandlerModel result) {
      UpdateCurrentVersion(result.getJsonResponse());
    }
  }

  class DownloadNewVersion extends AsyncTask<String, Integer, HttpHandlerModel> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    protected void onProgressUpdate(Integer... progress) {
      super.onProgressUpdate(progress);
      String msg = "";
      if (progress[0] > 99) {
        msg = "Finishing... ";

      } else {

        msg = "Downloading... " + progress[0] + "%";
      }
      downloadButton.setText(msg);
    }

    @Override
    protected HttpHandlerModel doInBackground(String... urls) {
      HttpHandlerModel passdata = new HttpHandlerModel();
      try {
        URL url = new URL(urls[0]);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        //  c.setDoOutput(true);
        c.connect();
        String PATH = Environment.getExternalStorageDirectory() + "/Download/";
        File file = new File(PATH);
        file.mkdirs();
        File outputFile = new File(file, "Pristine.apk");
        if (outputFile.exists()) {
          outputFile.delete();
        }

        FileOutputStream fos = new FileOutputStream(outputFile);
        InputStream is = c.getInputStream();

        int total_size = c.getContentLength();//1431692;//size of apk

        byte[] buffer = new byte[1024];
        int len1 = 0;
        int per = 0;
        int downloaded = 0;
        while ((len1 = is.read(buffer)) != -1) {
          fos.write(buffer, 0, len1);
          downloaded += len1;
          per = downloaded * 100 / total_size;
          publishProgress(per);
        }
        fos.close();
        is.close();
        passdata.setConnectStatus(true);
        passdata.setJsonResponse("Success Download");
        return passdata;
      } catch (Exception e) {
        downloadData = false;
        passdata.setConnectStatus(false);
        passdata.setJsonResponse(e.getMessage());
        return passdata;
      }
    }

    @Override
    protected void onPostExecute(HttpHandlerModel result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      if (result.isConnectStatus()) {
        OpenNewVersion();
        Toast.makeText(getActivity(), "Update Done",
                Toast.LENGTH_SHORT).show();
        errorCode.setText("");
        downloadData = false;
        downloadButton.setText("Download");

      } else {
        errorCode.setText(result.getJsonResponse() + " Api Error please check Apk path on web.");
        downloadData = false;
        downloadButton.setText("Download");
      }

    }


  }
}
