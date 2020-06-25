package com.e.belle.ui.vieworder;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e.belle.R;

import java.util.List;

public class OrderListAdapter extends BaseAdapter {
    private List<ViewOrderFragment.ViewOrderModel> listdata;
    private Activity activity;

    public OrderListAdapter(Activity activity, List<ViewOrderFragment.ViewOrderModel> listdata) {
        super();
        this.listdata = listdata;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.view_order_list, null);
        TextView tv_schedule_visit_date = convertView.findViewById(R.id.tv_schedule_visit_date);
        TextView tv_retailer = convertView.findViewById(R.id.tv_retailer);
        TextView tv_area = convertView.findViewById(R.id.tv_area);
        TextView tv_status = convertView.findViewById(R.id.tv_status);
        TextView tv_order_qty = convertView.findViewById(R.id.tv_order_qty);
        TextView tv_dispatch_qty = convertView.findViewById(R.id.tv_dispatch_qty);

        tv_schedule_visit_date.setText(listdata.get(position).schedule_visit_date);
        tv_retailer.setText(listdata.get(position).retailer);
        tv_area.setText(listdata.get(position).area);
        tv_status.setText(listdata.get(position).status);
        tv_order_qty.setText(listdata.get(position).order_qty);
        tv_dispatch_qty.setText(listdata.get(position).dispatch_qty);
        if(listdata.get(position).status!=null && listdata.get(position).status.equalsIgnoreCase("Dispatched")){
          convertView.setBackgroundColor(Color.parseColor("#D5F5E3"));
        }else  if(listdata.get(position).status!=null && listdata.get(position).status.equalsIgnoreCase("Rejected")){
            convertView.setBackgroundColor(Color.parseColor("#F5D7D5"));
        }
        return convertView;

    }

}
