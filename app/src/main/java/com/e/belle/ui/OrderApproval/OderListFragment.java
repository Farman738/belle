package com.e.belle.ui.OrderApproval;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.Dashboard;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.Model.AsyModel;
import com.e.belle.R;
import com.e.belle.SessionManagment.SessionManagement;
import com.e.belle.ui.ApprovalModel.ApprovalModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class OderListFragment extends Fragment {

    public ApprovalModel approvalModel;
    SessionManagement sessionManagement;
    ProgressDialog progress;
    private ListView list_item;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_order_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intializeVariables();

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ApproveRejectOrder approveRejectOrder = new ApproveRejectOrder();
                    Bundle args = new Bundle();
                    args.putString("LocalData", new Gson().toJson(list_item.getAdapter().getItem(position)));
                    approveRejectOrder.setArguments(args);
                    loadFragment(approveRejectOrder, "Approve Reject");

                } catch (Exception e) {
                }
            }
        });

    }

    private void intializeVariables() {

        list_item = getView().findViewById(R.id.list_item);

        Bind_APIListData();
    }

    private void Bind_APIListData() {
        sessionManagement = new SessionManagement(getActivity());
        try {

            AsyModel objAsy = new AsyModel(AllApiLinks.ApprovalList + sessionManagement.getUserId(), null, "Approval");
            new HitToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objAsy);
        } catch (Exception e) {

        }
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
        ((Dashboard) getActivity()).getSupportActionBar().setTitle("Order List");
    }

    private class HitToServer extends AsyncTask<AsyModel, Void, HttpHandlerModel> {
        OrderListAdapter orderListAdapter;
        private GlobalPostingMethod hitObj = new GlobalPostingMethod();
        private String flagOfAction;

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
            progress.dismiss();
            bindResponseData(result, flagOfAction);
        }

        private void bindResponseData(HttpHandlerModel result, String flagOfAction) {
            try {
                if (result.isConnectStatus()) {
                    if (flagOfAction.equalsIgnoreCase("Approval")) {
                        try {
                            ArrayList<ApprovalModel> approvalModel = new Gson().fromJson(result.getJsonResponse(), new TypeToken<ArrayList<ApprovalModel>>() {
                            }.getType());

                            if (approvalModel.size() > 0) {

                                if (approvalModel.get(0).condition) {

                                    orderListAdapter = new OrderListAdapter(getActivity(), approvalModel);
                                    list_item.setAdapter(orderListAdapter);
                                } else {

                                    Toast.makeText(getActivity(), approvalModel.get(0).message, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "No Approval Pending.", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {

                        }
                    }
                } else {
                    Toast.makeText(getActivity(), result.getJsonResponse(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }
        }
    }
}