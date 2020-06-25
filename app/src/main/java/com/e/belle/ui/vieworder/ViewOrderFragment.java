package com.e.belle.ui.vieworder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Dashboard;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.LoginSingUpActivity.LoginActivity;
import com.e.belle.Model.AsyModel;
import com.e.belle.Model.LoginModel;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;

public class ViewOrderFragment extends Fragment {
    public ViewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_order, container, false);
    }

    EditText et_fromDate, et_todate;
    boolean display_date = false;
    Button submit_button;
    SessionManagement sessionManagement;
    ListView order_listview;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManagement = new SessionManagement(getActivity());
        et_fromDate = view.findViewById(R.id.et_fromDate);
        et_todate = view.findViewById(R.id.et_todate);
        submit_button = view.findViewById(R.id.submit_button);
        order_listview=view.findViewById(R.id.order_listview);
        et_fromDate.setOnTouchListener((v, event) -> {
            if (!display_date) {
                display_date = true;
                displayDate(et_fromDate);
            }
            return true;
        });
        et_todate.setOnTouchListener((v, event) -> {
            if (!display_date) {
                display_date = true;
                displayDate(et_todate);
            }
            return true;
        });
        submit_button.setOnClickListener(v -> {
            new HitToServer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                    new AsyModel(AllApiLinks.GetAppOrderDetail + sessionManagement.getUserId()
                            + "&date1=" + et_fromDate.getText().toString() + "&date2=" + et_todate.getText().toString(), null, "getViewOrder"));
        });
    }

    public void displayDate(EditText edit_date) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Date");
        builder.setIcon(R.drawable.ic_date_range_pink_24dp);
        builder.setCancelable(false);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.date_picker_view, null);
        MaterialCalendarView calendarView = customLayout.findViewById(R.id.calendarView);
        TextView tv_selected_date = customLayout.findViewById(R.id.tv_selected_date);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_date.setText(tv_selected_date.getText());
                display_date = false;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                display_date = false;
            }
        });
        builder.setView(customLayout);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        // add a button
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            tv_selected_date.setText(date.getYear() + "/" + (date.getMonth() + 1) + "/" + date.getDay());
        });
        dialog.show();
    }

    private class HitToServer extends AsyncTask<AsyModel, Void, HttpHandlerModel> {
        ProgressDialog progress;
        private GlobalPostingMethod hitObj = new GlobalPostingMethod();
        private String flagOfAction;

        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
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

    private void bindResponse(HttpHandlerModel result, String flagOfAction) {
        try {
            if (result.isConnectStatus()) {
                if (flagOfAction.equalsIgnoreCase("getViewOrder")) {
                    List<ViewOrderModel> resultList = new Gson().fromJson(result.getJsonResponse(), new TypeToken<List<ViewOrderModel>>() {
                    }.getType());
                    if(resultList.size()>0 && resultList.get(0).condition){

                        OrderListAdapter adapter = new OrderListAdapter(getActivity(), resultList);
                        order_listview.setAdapter(adapter);
                    }else{
                        Snackbar.make(submit_button, resultList.get(0).message, Snackbar.LENGTH_LONG).show();
                    }
                }
            } else {
                Snackbar.make(submit_button, result.getJsonResponse(), Snackbar.LENGTH_LONG).show();

            }
        } catch (Exception e) {

        }

    }

    public class ViewOrderModel {
        public boolean condition;
        public String sr_no;
        public String retailer;
        public String area;
        public String status;
        public String order_qty;
        public String dispatch_qty;
        public String distributor;
        public String schedule_visit_date;
        public String message;
    }

}