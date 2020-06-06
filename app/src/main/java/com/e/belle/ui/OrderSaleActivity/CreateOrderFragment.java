package com.e.belle.ui.OrderSaleActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Dashboard;
import com.e.belle.ForAnimation.MyBounceInterpolator;
import com.e.belle.Global.FilePath;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.MultipartUtility;
import com.e.belle.MainActivity;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.e.belle.ui.Model.ActivityModel;
import com.e.belle.ui.Model.GetRetailerDetailsModel;
import com.e.belle.ui.Model.GetRetailerModel;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class CreateOrderFragment extends Fragment {

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1222;
    final static int SELECT_FILE = 20;
    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView tv_capture, tv_gallery;
    private SessionManagement sessionManagement;
    TextView tv_success_username, tv_success_userday, tv_success_userArea, tv_success_userRetailer;
    TextView tv_activity_username, tv_activity_usergrade, tv_activity_usercontact, tv_activity_useraddress;
    private MaterialBetterSpinner mbs_manufacturing_days, mbs_area, mbs_retailer;
    ImageSwipeAdapter imageSwipeAdapter;
    ViewPager viewPager;
    Uri imageUri = null;
    ArrayList<String> file_url = new ArrayList<String>();
    public GetRetailerDetailsModel getRetailerDetailsModels;
    Button upload_ok_btn, goToHome_btn, btn_upload_order, btn_noOrder, btn_ok_no_order;
    ArrayList<Bitmap> imagebitmaps = new ArrayList<Bitmap>();
    String order_no = "";
    CardView cardViewd_show_upload_order_design, carView_show_noOder_design;
    MaterialBetterSpinner mbs_noOrder;
    private String noOrder[] = {"Last Oder Not Received", "Next Week", "Owner Not Available", "Payment Issue", "Shop Closed", "Others"};


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_order, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intializeVariables();
        sessionManagement = new SessionManagement(getActivity().getApplicationContext());

        try {
            getRetailerDetailsModels = new Gson().fromJson(getArguments().getString("LocalData"), GetRetailerDetailsModel.class);

            AssignValues();
        } catch (Exception e) {
        }

        // todo for capture image

        tv_capture.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_animedit);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                tv_capture.startAnimation(myAnim);
                return true;

            }
        });


        tv_gallery.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_animedit);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                tv_gallery.startAnimation(myAnim);
                return true;
            }
        });


        tv_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readFile();


//        btn_ok_no_order.setVisibility(View.GONE);
                cardViewd_show_upload_order_design.setVisibility(View.VISIBLE);


            }
        });

        tv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "SELECT FILE"), SELECT_FILE);
                } catch (Exception e) {

                }
            }
        });

        imagebitmaps.add(null);
        viewPager = (ViewPager) getView().findViewById(R.id.vp_image);
        imageSwipeAdapter = new ImageSwipeAdapter(getActivity(), imagebitmaps, file_url, viewPager);
        viewPager.setAdapter(imageSwipeAdapter);

    }

    public void readFile() {
        try {
            /*************************** Camera Intent Start ************************/

            // Define the file-name to save photo taken by Camera activity

            String fileName = "Camera_Example.jpg";

            // Create parameters for Intent with filename

            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.TITLE, fileName);

            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

            // imageUri is the current activity attribute, define and save it for later usage

            imageUri = getActivity().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/


            // Standard Intent action that can be sent to have the camera
            // application capture an image and return it.

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            /*************************** Camera Intent End ************************/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            btn_ok_no_order.setVisibility(View.GONE);
        }
    }

    //  int PICK_IMAGE_MULTIPLE = 1;
    public String previous_selectimege = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (previous_selectimege.equalsIgnoreCase("") || !previous_selectimege.equalsIgnoreCase("Capture")) {
                    imagebitmaps.clear();
                    file_url.clear();
                }
                file_url.add(FilePath.getPath(getActivity(), imageUri));
                Bitmap theImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imagebitmaps.add(theImage);
                previous_selectimege = "Capture";
            } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK && null != data) {
                if (imagebitmaps.size() > 0) {
                    imagebitmaps.clear();
                    file_url.clear();
                }
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        imagebitmaps.add(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                data.getClipData().getItemAt(i).getUri()));
                        file_url.add(FilePath.getPath(getActivity().getApplicationContext(), data.getClipData().getItemAt(i).getUri()));
                    }
                } else {
                    Uri uri = data.getData();
                    String path = FilePath.getPath(getActivity().getApplicationContext(), uri);
                    imagebitmaps.add(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                    file_url.add(path);
                }
                previous_selectimege = "Select";
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        } finally {
            if (imagebitmaps.size() > 0) {
                imageSwipeAdapter = new ImageSwipeAdapter(getActivity(), imagebitmaps, file_url, viewPager);
                viewPager.setAdapter(imageSwipeAdapter);
                cardViewd_show_upload_order_design.setVisibility(View.VISIBLE);
            } else {
                imagebitmaps.add(null);
                imageSwipeAdapter = new ImageSwipeAdapter(getActivity(), imagebitmaps, file_url, viewPager);
                viewPager.setAdapter(imageSwipeAdapter);
                cardViewd_show_upload_order_design.setVisibility(View.VISIBLE);
            }
        }


    }

    private void AssignValues() {
        tv_activity_username.setText(getRetailerDetailsModels.owner1_name);
        tv_activity_usergrade.setText(getRetailerDetailsModels.grade);
        tv_activity_usercontact.setText(getRetailerDetailsModels.owner1_mobile);
        tv_activity_useraddress.setText(getRetailerDetailsModels.line1);
    }

    private void intializeVariables() {


        mbs_manufacturing_days = getView().findViewById(R.id.mbs_manufacturing_days);
        mbs_area = getView().findViewById(R.id.mbs_area);
        mbs_retailer = getView().findViewById(R.id.mbs_retailer);
        tv_activity_username = getView().findViewById(R.id.tv_activity_username);
        tv_activity_usergrade = getView().findViewById(R.id.tv_activity_usergrade);
        tv_activity_usercontact = getView().findViewById(R.id.tv_activity_usercontact);
        tv_activity_useraddress = getView().findViewById(R.id.tv_activity_useraddress);
        cardViewd_show_upload_order_design = getView().findViewById(R.id.cardViewd_show_upload_order_design);
        carView_show_noOder_design = getView().findViewById(R.id.carView_show_noOder_design);
        tv_capture = getView().findViewById(R.id.tv_capture);
        tv_gallery = getView().findViewById(R.id.tv_gallery);

        upload_ok_btn = getView().findViewById(R.id.upload_ok_btn);


        //for show capture image


        // no order btn button
        btn_ok_no_order = getView().findViewById(R.id.btn_ok_no_order);

        btn_ok_no_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    new UploadFileAsyTask(AllApiLinks.CreateOrderHeader, mbs_noOrder.getText().toString(), "no_order", "No").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getActivity(), Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //todo for upload order button open capture and gallery UI
        btn_upload_order = getView().findViewById(R.id.btn_upload_order);

        btn_upload_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewd_show_upload_order_design.setVisibility(View.VISIBLE);
                btn_ok_no_order.setVisibility(View.GONE);
                carView_show_noOder_design.setVisibility(View.GONE);

            }
        });

        //todo for No Order button open mbs_material spinner UI

        btn_noOrder = getView().findViewById(R.id.btn_noOrder);

        btn_noOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carView_show_noOder_design.setVisibility(View.VISIBLE);
                cardViewd_show_upload_order_design.setVisibility(View.GONE);
                btn_ok_no_order.setVisibility(View.GONE);


                mbs_noOrder = getView().findViewById(R.id.mbs_noOrder);
                ArrayAdapter<String> adapter_noOrder = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, noOrder);
                adapter_noOrder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mbs_noOrder.setAdapter(adapter_noOrder);


                mbs_noOrder.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btn_ok_no_order.setVisibility(View.VISIBLE);

                    }
                });
//        mbs_order_bind();
            }
        });


        upload_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
//          btn_ok_no_order.setVisibility(View.GONE);
                    new UploadFileAsyTask(AllApiLinks.CreateOrderHeader, "", "upload_order", "Yes").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } finally {

                }

            }
        });

    }


    public class UploadFileAsyTask extends AsyncTask<String, String, String> {
        public GlobalPostingMethod globalPostingMethod = new GlobalPostingMethod();
        String CreateCustomerUrl;
        private ProgressDialog progress;
        String no_order_reason;
        String order_status;
        String order_btn;

        public UploadFileAsyTask() {
        }

        public UploadFileAsyTask(String CreateCustomer, String no_order_reason, String order_btn, String order_status) {
            this.CreateCustomerUrl = CreateCustomer;
            this.no_order_reason = no_order_reason;
            this.order_btn = order_btn;
            this.order_status = order_status;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Please Wait..");
            progress.setCanceledOnTouchOutside(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage("Image Procesing " + values[0]);
        }

        @Override
        protected String doInBackground(String... customerAddSendModels) {
            try {

                MultipartUtility multipartUtility = new MultipartUtility(CreateCustomerUrl);
                multipartUtility.addFormField("visit_day", getRetailerDetailsModels.visit_day);
                multipartUtility.addFormField("retailer_id", getRetailerDetailsModels.retailer_id);
                multipartUtility.addFormField("area_id", getRetailerDetailsModels.area_id);
                multipartUtility.addFormField("order_status", order_status);
                multipartUtility.addFormField("no_order_reason", no_order_reason);
                multipartUtility.addFormField("created_by", sessionManagement.getUserName());
                for (int i = 0; i < file_url.size(); i++) {
                    multipartUtility.addFilePart("imageUrl", new File(file_url.get(i)));
                }

                String imageResponse = multipartUtility.finish().getJsonResponse();

                JSONArray array = new JSONArray(imageResponse);
                JSONObject jsonObject = new JSONObject(array.get(0).toString());

                if (jsonObject.getString("condition").equalsIgnoreCase("TRUE")) {
                    order_no = jsonObject.getString("order_no");
                    return "true-" + order_btn;

                } else {
                    return "false-" + order_btn;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            progress.dismiss();
            try {

                String[] get_response = response.split("-", 2);

                if (get_response[0].equalsIgnoreCase("true")) {
                    if (get_response[1].equalsIgnoreCase("upload_order"))
                        SetSuccessfullMessagePopup(order_no);
//          btn_ok_no_order.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getActivity(), "Not Created.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {


            }
        }
    }

    public void onResume() {
        super.onResume();
        ((Dashboard) getActivity()).getSupportActionBar().setTitle("Create Order");
    }

    private void SetSuccessfullMessagePopup(String order_no) {
        try {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View successmessaePopupView = inflater.inflate(R.layout.activity_successfully, null);
            final AlertDialog dailog = new AlertDialog.Builder(getActivity()).create();

            tv_success_username = successmessaePopupView.findViewById(R.id.tv_success_username);
            tv_success_userday = successmessaePopupView.findViewById(R.id.tv_success_userday);
            tv_success_userArea = successmessaePopupView.findViewById(R.id.tv_success_userArea);
            tv_success_userRetailer = successmessaePopupView.findViewById(R.id.tv_success_userRetailer);

            tv_success_username.setText(sessionManagement.getUserName());
            tv_success_userday.setText(getRetailerDetailsModels.day_market);
            tv_success_userArea.setText(getRetailerDetailsModels.line1);
            tv_success_userRetailer.setText(getRetailerDetailsModels.firm_name);

//      tv_user_success_image.setImageBitmap(!imagebitmaps.isEmpty() && imagebitmaps.size() > 0 ? imagebitmaps.get(0) : null);
            dailog.setView(successmessaePopupView);
            dailog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            dailog.setCancelable(false);
            dailog.show();

            goToHome_btn = successmessaePopupView.findViewById(R.id.goToHome_btn);
            goToHome_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
        } finally {
//      btn_ok_no_order.setVisibility(View.GONE);

        }
    }


}


