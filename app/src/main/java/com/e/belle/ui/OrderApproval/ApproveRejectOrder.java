package com.e.belle.ui.OrderApproval;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Dashboard;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.Model.AsyModel;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.e.belle.ui.ApprovalModel.ApprovalModel;
import com.e.belle.ui.ApprovalModel.OrderApprovalListModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class ApproveRejectOrder extends Fragment {

     TextView approve_reject_username,approve_reject_area,approve_reject_retailer,approve_reject_visitday,approve_reject_region,tv_total_quantity;
     Button approve_btn,reject_btn;
     ApprovalModel approvalModel;
     SessionManagement sessionManagement;
     SwipeRefreshLayout swipeLayout;
     ProgressDialog progress;
     OrderApprovalListModel orderApprovalListModel;
     OrderApprovalListAdpater orderApprovalListAdpater;
     ListView order_list;
     int orderno = 0;
     TextView message_header,message,approve_message;
     EditText edit_percnt_approve;
     TextInputLayout til_reason;
     EditText edt_review;
    ProgressBar pb_horizontal;
    int total_quantity;


    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approve_reject_order,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialization();
//        bind_ScrollRefresh();


        try {

            approvalModel = new Gson().fromJson(getArguments().getString("LocalData"), ApprovalModel.class);
            AssignValues();

        }catch (Exception e){

        }

        bind_crop_list();

     reject_btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             SetRejectlMessagePopup();
         }

     });

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetApprovalMessage();
            }
        });

    }



    private void bind_crop_list(){
        sessionManagement = new SessionManagement(getActivity());
        try {

         AsyModel objAsy = new AsyModel(AllApiLinks.OrderListLine+approvalModel.order_no,null,"OrderListLine");
         new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,objAsy);
        }catch (Exception e){

        }
    }

    private void AssignValues() {

        approve_reject_username.setText(approvalModel.salesman);
        approve_reject_visitday.setText(approvalModel.schedule_visit_day + " , " + approvalModel.schedule_visit_time);
        approve_reject_area.setText(approvalModel.area);
        approve_reject_retailer.setText(approvalModel.retailer);
        approve_reject_region.setText(approvalModel.region);
    }

    private void initialization() {

        approve_reject_username = getView().findViewById(R.id.approve_reject_username);
        approve_reject_visitday = getView().findViewById(R.id.approve_reject_visitday);
        approve_reject_retailer = getView().findViewById(R.id.approve_reject_retailer);
        approve_reject_area = getView().findViewById(R.id.approve_reject_area);
        approve_reject_region = getView().findViewById(R.id.approve_reject_region);
        order_list = getView().findViewById(R.id.order_list);
        tv_total_quantity = getView().findViewById(R.id.tv_total_quantity);

        approve_btn = getView().findViewById(R.id.approve_btn);
        reject_btn = getView().findViewById(R.id.reject_btn);
    }


  private void SetApprovalMessage(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View approvePopupmessage = inflater.inflate(R.layout.approve_popup,null);
      final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
      approve_message =  approvePopupmessage.findViewById(R.id.approve_message);
      edit_percnt_approve = approvePopupmessage.findViewById(R.id.edit_percnt_approve);
      pb_horizontal = approvePopupmessage.findViewById(R.id.pb_horizontal);

     final Button approve_btn_cancel = approvePopupmessage.findViewById(R.id.approve_btn_cancel);
     final Button approve_btn_ok = approvePopupmessage.findViewById(R.id.approve_btn_ok);

      dialog.setView(approvePopupmessage);
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
      dialog.setCancelable(false);
      dialog.show();

      approve_btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              try {
                  dialog.dismiss();
              }catch (Exception e){

              }
          }
      });

      approve_btn_ok.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              try {

                  JSONObject postedJson = new JSONObject();
                  postedJson.put("order_no", approvalModel.order_no);
                  postedJson.put("status", "Approved");
                  postedJson.put("reason","");
                  postedJson.put("percentage_approval",edit_percnt_approve.getText().toString());
                  postedJson.put("approve_reject_by", sessionManagement.getUserName());
                  new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new AsyModel(
                          AllApiLinks.CancelOkOrder, postedJson, "ApproveOrder"
                  ));
                  dialog.dismiss();

              }catch (Exception e){

              }
          }
      });
  }

    private void SetRejectlMessagePopup(){

       LayoutInflater inflater = getActivity().getLayoutInflater();
       View rejectPopupMessage = inflater.inflate(R.layout.reject_popup,null);
       final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();

         message_header =  rejectPopupMessage.findViewById(R.id.message_header);
         message =  rejectPopupMessage.findViewById(R.id.message);
          til_reason =  rejectPopupMessage.findViewById(R.id.til_reason);
          edt_review =  rejectPopupMessage.findViewById(R.id.edt_reason);

        final Button btn_cancel = rejectPopupMessage.findViewById(R.id.btn_cancel);
        final Button btn_ok = rejectPopupMessage.findViewById(R.id.btn_ok);

        til_reason.setVisibility(View.VISIBLE);
        message.setText("Are You Sure ? You Want To Reject Customer");
        dialog.setView(rejectPopupMessage);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.setCancelable(false);
        dialog.show();

         btn_cancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     dialog.dismiss();
                 }catch (Exception e){

                 }
             }
         });

      btn_ok.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              try {

                  if(edt_review.getText().toString().equalsIgnoreCase("")){
                      edt_review.setError("Please Fill Rejected Reason");
                      edt_review.requestFocus();
                  } else {

                      JSONObject postedJson = new JSONObject();
                      postedJson.put("order_no", approvalModel.order_no);
                      postedJson.put("status", "Rejected");
                      postedJson.put("reason",edt_review.getText().toString());
                      postedJson.put("approve_reject_by", sessionManagement.getUserName());
                      new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new AsyModel(
                                AllApiLinks.CancelOkOrder, postedJson, "ApproveReject"
                       ));
                     dialog.dismiss();
                  }
              } catch (Exception e){

              }
          }
      });
    }


public class HitToServer extends AsyncTask<AsyModel, Void, HttpHandlerModel>{

   private GlobalPostingMethod hitObj = new GlobalPostingMethod();
   private String flagOfAction;

   protected  void onPreExecute() {
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
           

           URL postingUrl = hitObj.createUrl(asyModels[0].getPostingUrl());
           flagOfAction = asyModels[0].getFlagOfAction();
           if(asyModels[0].getPostingJson()== null){
               return hitObj.getHttpRequest(postingUrl);
           } else {
               return hitObj.postHttpRequest(postingUrl, asyModels[0].getPostingJson());
           }

       }catch (Exception e){
           return hitObj.setReturnMessage(false,"Problem retrieving the user JSON results." + e.getMessage());
       }

    }
   protected void onPostExecute(HttpHandlerModel result) {
       super.onPostExecute(result);
       progress.dismiss();
       bind_ResponseData(result,flagOfAction);
   }

    private void bind_ResponseData(HttpHandlerModel result, String flagOfAction) {

       try {

           if(result.isConnectStatus()){
               if(flagOfAction.equalsIgnoreCase("OrderListLine")) {

                   try {
                      ArrayList<OrderApprovalListModel> orderApprovalListModel = new Gson().fromJson(result.getJsonResponse(),
                              new TypeToken<ArrayList<OrderApprovalListModel>>(){}.getType());
                       if (orderApprovalListModel.size() > 0) {

                           if (orderApprovalListModel.get(0).condition) {
                               total_quantity=0;
                               orderApprovalListAdpater = new OrderApprovalListAdpater(getActivity(), orderApprovalListModel);
                               order_list.setAdapter(orderApprovalListAdpater);
//                               int count = 0;
                               for(int i = 0; i<orderApprovalListModel.size();i++){
                                   total_quantity+= Integer.parseInt(orderApprovalListModel.get(i).qty);
                               }
                               tv_total_quantity.setText(String.valueOf(total_quantity));

                           } else {

                               Toast.makeText(getActivity(), orderApprovalListModel.get(0).message, Toast.LENGTH_LONG).show();
                           }
                       } else {
                           Toast.makeText(getActivity(), "No Approval Pending.", Toast.LENGTH_LONG).show();
                       }

                   } catch (Exception e) {
                       e.printStackTrace();

                   }
               }else if(flagOfAction.equalsIgnoreCase("ApproveReject")) {

                   try {

                       JSONArray jsonArray = new JSONArray(result.getJsonResponse());
                       JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());

                       if(jsonObject.getString("condition").equalsIgnoreCase("true")){
                           OderListFragment orderlistfragment = new OderListFragment();
                           Bundle args = new Bundle();
                           orderlistfragment.setArguments(args);
                           loadFragment(orderlistfragment,"ApproveRejectOrder");
                       }

                       Toast.makeText(getActivity(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();


                   } catch (Exception e) {

                   }
               }else if(flagOfAction.equalsIgnoreCase("ApproveOrder")) {

                   try {

                       JSONArray jsonArray = new JSONArray(result.getJsonResponse());
                       JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());

                       if(jsonObject.getString("condition").equalsIgnoreCase("true")){
                           OderListFragment orderlistfragment = new OderListFragment();
                           Bundle args = new Bundle();
                           orderlistfragment.setArguments(args);
                           loadFragment(orderlistfragment,"ApproveRejectOrder");
                       }

                       Toast.makeText(getActivity(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();


                   } catch (Exception e) {

                   }
               }
           } else {
               Toast.makeText(getActivity(), result.getJsonResponse(), Toast.LENGTH_LONG).show();
           }

       }catch (Exception e){

       }
    }
}

private void loadFragment(Fragment newFragment, String FragmentName){

    FragmentManager fm = getFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();
    fragmentTransaction.replace(R.id.nav_host_fragment, newFragment).addToBackStack(null);
    fragmentTransaction.commit();
    getActivity().setTitle(FragmentName);
}

public void onResume(){
        super.onResume();
    ((Dashboard) getActivity()).getSupportActionBar().setTitle("Approve and Reject");
}
}
