package com.e.belle.ui.OrderApproval;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e.belle.R;
import com.e.belle.ui.ApprovalModel.ApprovalModel;

import java.util.ArrayList;

public class OrderListAdapter extends BaseAdapter {

    public ArrayList<ApprovalModel> list;
    public ArrayList<ApprovalModel> mStringFilterList;
    Activity activity;

    public OrderListAdapter(Activity activity, ArrayList<ApprovalModel> list) {
        super();
        this.activity = activity;
        this.list = list;
        this.mStringFilterList = list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.order_list_view, null);


        TextView order_Number = convertView.findViewById(R.id.tv_order_number);
        TextView order_userVisitday = convertView.findViewById(R.id.tv_order_visit_day);
        TextView order_userTime = convertView.findViewById(R.id.tv_order_usertime);
        TextView order_userRetailer = convertView.findViewById(R.id.tv_order_userretailer);
        TextView order_userStatus = convertView.findViewById(R.id.tv_order_userstatus);

        order_Number.setText(list.get(position).order_no + "("+ list.get(position).salesman +")");
        order_userVisitday.setText(list.get(position).schedule_visit_day);
        order_userTime.setText(list.get(position).schedule_visit_time);
        order_userRetailer.setText(list.get(position).retailer + " , "+list.get(position).area + " , " +list.get(position).region );
//        order_userRegion.setText(list.get(position).region);
        order_userStatus.setText(list.get(position).status);


        return convertView;
    }
    protected void publishResults(CharSequence constraint) {
        list = (ArrayList<ApprovalModel>)constraint;
        notifyDataSetChanged();
    }
}
