package com.e.belle.ui.OrderSaleActivity;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.Model.AsyModel;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.e.belle.ui.Model.ActivityModel;
import com.e.belle.ui.Model.GetAreaModel;
import com.e.belle.ui.Model.GetRetailerDetailsModel;
import com.e.belle.ui.Model.GetRetailerModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import androidx.fragment.app.FragmentTransaction;


public class ActivityFragment extends Fragment {

  final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
  final static int SELECT_FILE = 2;
  private static final int PERMISSION_REQUEST_CODE = 200;
  public ArrayList<ActivityModel> newactivityModels;
  public ArrayList<GetRetailerModel> newRetailerModel;
  public ArrayList<GetAreaModel> newAreaModel;
  Button create_order_btn;
  TextView  tv_activity_username, tv_activity_usergrade, tv_activity_usercontact, tv_activity_useraddress;
  String order_no = "";
  int areaid = 0, retailerId = 0, day_id = 0;
  private MaterialBetterSpinner mbs_manufacturing_days, mbs_area, mbs_retailer;
  private String[] month = {};
  private String[] area = {};
  private String[] retailer = {};
  private SessionManagement sessionManagement;
  private CardView cv_cust_details, cd_show_retailer_details;

  public void bind_amc_period(View view) {
    sessionManagement = new SessionManagement(getActivity().getApplicationContext());
    cv_cust_details = view.findViewById(R.id.cv_cust_details);
    cd_show_retailer_details = view.findViewById(R.id.cd_show_retailer_details);
    tv_activity_username = view.findViewById(R.id.tv_activity_username);
    tv_activity_usergrade = view.findViewById(R.id.tv_activity_usergrade);
    tv_activity_usercontact = view.findViewById(R.id.tv_activity_usercontact);
    tv_activity_useraddress = view.findViewById(R.id.tv_activity_useraddress);


    // for create order button
    create_order_btn = view.findViewById(R.id.create_order_btn);

    mbs_manufacturing_days = view.findViewById(R.id.mbs_manufacturing_days);
    mbs_area = view.findViewById(R.id.mbs_area);
    mbs_retailer = view.findViewById(R.id.mbs_retailer);

    ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, month);
    adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mbs_manufacturing_days.setAdapter(adapter_month);
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_activity, container, false);

  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    sessionManagement = new SessionManagement(getActivity());
    bind_amc_period(view);

    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("emp_id", sessionManagement.getUserId());
      AsyModel objAsy = new AsyModel(AllApiLinks.GetDayMarketList, jsonObject, "GetDays");
      new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objAsy);
    } catch (Exception ee) {

    }

    mbs_manufacturing_days.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {

        try {
          day_id =Integer.parseInt(s.toString().split(" \\(")[1].split("\\)")[0]);
          AsyModel objAsy = new AsyModel(AllApiLinks.GetArea + day_id+"&salesman_id="+sessionManagement.getUserId(), null, "GetArea");
          new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objAsy);
        } catch (Exception ex) {
        }finally {
          mbs_area.setText("");
          mbs_retailer.setText("");
//                    cv_cust_details.setVisibility(View.GONE);
          cd_show_retailer_details.setVisibility(View.GONE);

        }

      }
    });

    mbs_area.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {

        try {
          areaid = Integer.parseInt(s.toString().split(" \\(")[1].split("\\)")[0]);
          AsyModel objAsy = new AsyModel(AllApiLinks.GetRetailer + areaid+"&salesman_id="+sessionManagement.getUserId(), null, "GetRetailer");
          new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objAsy);

        } catch (Exception e) {

        }finally {
          mbs_retailer.setText("");
          cd_show_retailer_details.setVisibility(View.GONE);

        }
      }
    });

    mbs_retailer.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {

        try {

          retailerId = Integer.parseInt(s.toString().split(" \\[")[1].split("\\]")[0]);
          AsyModel objAsy = new AsyModel(AllApiLinks.GetRetailerDetails + retailerId, null, "GetRetailerDetails");
          new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objAsy);



        } catch (Exception e) {
        }finally {
          cd_show_retailer_details.setVisibility(View.VISIBLE);

        }
      }

    });



    create_order_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          CreateOrderFragment createOrderFragment = new CreateOrderFragment();
          GetRetailerDetailsModel getRetailerDetailsModel = new GetRetailerDetailsModel();
          getRetailerDetailsModel.owner1_name = tv_activity_username.getText().toString();
          getRetailerDetailsModel.grade = tv_activity_usergrade.getText().toString();
          getRetailerDetailsModel.owner1_mobile = tv_activity_usercontact.getText().toString();
          getRetailerDetailsModel.line1 = tv_activity_useraddress.getText().toString();
          getRetailerDetailsModel.day_market = mbs_manufacturing_days.getText().toString().split(" \\(")[1].split("\\)")[0];
          getRetailerDetailsModel.firm_name = mbs_retailer.getText().toString();
          getRetailerDetailsModel.visit_day = String.valueOf(day_id);
          getRetailerDetailsModel.area_id = String.valueOf(areaid);
          getRetailerDetailsModel.retailer_id = String.valueOf(retailerId);
          Bundle args = new Bundle();
          args.putString("LocalData", new Gson().toJson(getRetailerDetailsModel));
          createOrderFragment.setArguments(args);
          loadFragment(createOrderFragment, "Create Order");
        }catch (Exception ex){

        }
      }

    });

  }
  public void loadFragment(Fragment newFragment, String FragmentName) {
    FragmentManager fm = getFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();
    fragmentTransaction.replace(R.id.nav_host_fragment, newFragment).addToBackStack(null);
    fragmentTransaction.commit();
    getActivity().setTitle(FragmentName);
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle("Activity");
  }



  public class HitToServer extends AsyncTask<AsyModel, Void, HttpHandlerModel> {
    private GlobalPostingMethod hitObj = new GlobalPostingMethod();
    private String flagOfAction;
    private ProgressDialog progress;
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
      bindResponse(result, flagOfAction);
    }
    private void bindResponse(HttpHandlerModel result, String flagOfAction) {
      try {
        if (result.isConnectStatus()) {
          if (flagOfAction.equalsIgnoreCase("GetDays")) {
            final ArrayList<ActivityModel> activityModels = new Gson().fromJson(result.getJsonResponse(), new TypeToken<ArrayList<ActivityModel>>() {
            }.getType());
            if (activityModels.get(0).condition) {
              newactivityModels = activityModels;
              final ArrayList<String> dayMarket = new ArrayList<String>();
              for (int i = 0; i < activityModels.size(); i++) {
                dayMarket.add(activityModels.get(i).day_market+" ("+activityModels.get(i).id+")");
              }
              Collections.sort(dayMarket, String.CASE_INSENSITIVE_ORDER);
              final ArrayAdapter<String> adapter_days = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dayMarket);
              adapter_days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              mbs_manufacturing_days.setAdapter(adapter_days);

            } else {
              Toast.makeText(getActivity(), activityModels.get(0).message, Toast.LENGTH_LONG).show();
            }
          } else if (flagOfAction.equalsIgnoreCase("GetArea")) {
            ArrayList<GetAreaModel> areaModels = new Gson().fromJson(result.getJsonResponse(), new TypeToken<ArrayList<GetAreaModel>>() {
            }.getType());
            if (areaModels.get(0).condition) {

              newAreaModel = areaModels;

              ArrayList<String> areaName = new ArrayList<String>();

              for (int i = 0; i < areaModels.size(); i++) {
                areaName.add(areaModels.get(i).area_name+" ("+areaModels.get(i).area_id+")");
              }
              Collections.sort(areaName, String.CASE_INSENSITIVE_ORDER);

              ArrayAdapter<String> adapter_area = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, areaName);
              adapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              mbs_area.setAdapter(adapter_area);
              mbs_area.setText("");
              cv_cust_details.setVisibility(View.VISIBLE);

            } else {
//                        Toast.makeText(getActivity(), areaModels.get(0).message, Toast.LENGTH_LONG).show();
            }
          } else if (flagOfAction.equalsIgnoreCase("GetRetailer")) {
            ArrayList<GetRetailerModel> getRetailerModels = new Gson().fromJson(result.getJsonResponse(), new TypeToken<ArrayList<GetRetailerModel>>() {
            }.getType());
            if (getRetailerModels.get(0).condition) {

              newRetailerModel = getRetailerModels;


              ArrayList<String> retailer = new ArrayList<String>();

              for (int i = 0; i < getRetailerModels.size(); i++) {
                retailer.add(getRetailerModels.get(i).firm_name+" ["+getRetailerModels.get(i).retailer_id+"]");
              }
              Collections.sort(retailer, String.CASE_INSENSITIVE_ORDER);
              ArrayAdapter<String> adapter_retailer = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, retailer);
              adapter_retailer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              mbs_retailer.setAdapter(adapter_retailer);
              mbs_retailer.setText("");
              cd_show_retailer_details.setVisibility(View.GONE);

            } else {
//                        Toast.makeText(getActivity(), getRetailerModels.get(0).message, Toast.LENGTH_LONG).show();
            }
          } else if (flagOfAction.equalsIgnoreCase("GetRetailerDetails")) {

            ArrayList<GetRetailerDetailsModel> getRetailerDetailsModels = new Gson().fromJson(result.getJsonResponse(), new TypeToken<ArrayList<GetRetailerDetailsModel>>() {
            }.getType());

            if (getRetailerDetailsModels.get(0).condition) {


              cd_show_retailer_details.setVisibility(View.VISIBLE);
              tv_activity_username.setText(getRetailerDetailsModels.get(0).owner1_name);
              tv_activity_username.setText(getRetailerDetailsModels.get(0).firm_name);
              tv_activity_usergrade.setText(getRetailerDetailsModels.get(0).grade);
              tv_activity_usercontact.setText(getRetailerDetailsModels.get(0).owner1_mobile);
              tv_activity_useraddress.setText(getRetailerDetailsModels.get(0).line1);
//              show_create_button.setVisibility(View.VISIBLE);




            } else {

//                        Toast.makeText(getActivity(), getRetailerDetailsModels.get(0).message, Toast.LENGTH_LONG).show();
            }
          }
        } else {
         Toast.makeText(getActivity(), result.getJsonResponse(), Toast.LENGTH_LONG).show();
        }
      } catch (Exception e) {

      }finally {
        progress.dismiss();
      }
    }
  }
}


