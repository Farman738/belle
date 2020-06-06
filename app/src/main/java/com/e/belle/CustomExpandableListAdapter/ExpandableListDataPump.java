package com.e.belle.CustomExpandableListAdapter;

import android.content.Context;

import com.e.belle.Model.LoginModel;
import com.e.belle.SessionManagment.SessionManagement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListDataPump {

//
    public static Map<String, List<String>> getData(Context context) {
        Map<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        try {
            ArrayList<LoginModel.menu_name> menu = new Gson().fromJson(new SessionManagement(context).getMenu(), new TypeToken<ArrayList<LoginModel.menu_name>>() {
            }.getType());
            for (int i = 0; i < menu.size(); i++) {
                List<String> menulist = new ArrayList<String>();
                for (int j = 0; j < menu.get(i).children.size(); j++) {
                    menulist.add(menu.get(i).children.get(j).title);
                }
                expandableListDetail.put(menu.get(i).title, menulist);
            }
            return expandableListDetail;
        }catch (Exception e){
            return expandableListDetail;
        }
    }

//    public static Map<String, List<String >> getData() {
//
//        Map<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
//
//        List<String> dashboard = new ArrayList<String>();
//        dashboard.add("Dashboard");
//
//        List<String> request = new ArrayList<String>();
//        request.add("Create Request");
//        request.add("All Request");
//        request.add("Re-Schedule Request");
//        request.add("Cancel Request");
//
//        List<String> item = new ArrayList<String>();
//        item.add("ManiFest");
//
//        List<String> feedback = new ArrayList<String>();
//        //feedback.add("Sort Order");
//
//        expandableListDetail.put("Dashboard", dashboard);
//        expandableListDetail.put("Request", request);
//        expandableListDetail.put("Items", item);
//        expandableListDetail.put("Feedback", feedback);
//
//        return expandableListDetail;
//    }
}
