package com.e.belle.ui.OrderApproval;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e.belle.R;
import com.e.belle.ui.ApprovalModel.OrderApprovalListModel;

import java.util.ArrayList;

public class OrderApprovalListAdpater extends BaseAdapter {

    public ArrayList<OrderApprovalListModel> list;
    public ArrayList<OrderApprovalListModel> mStringFilterList;
    Activity activity;

    public OrderApprovalListAdpater(Activity activity, ArrayList<OrderApprovalListModel> list) {
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
        convertView = layoutInflater.inflate(R.layout.approval_order_list_view, null);

        TextView tv_order_approval_art_no = convertView.findViewById(R.id.tv_order_approval_art_no);
        TextView tv_order_approval_brand = convertView.findViewById(R.id.tv_order_approval_brand);
        TextView tv_order_approval_color = convertView.findViewById(R.id.tv_order_approval_color);
        TextView tv_order_approval_size = convertView.findViewById(R.id.tv_order_approval_size);
        TextView tv_order_approval_qty = convertView.findViewById(R.id.tv_order_approval_qty);

        tv_order_approval_art_no.setText(list.get(position).art_no);
        tv_order_approval_brand.setText(list.get(position).brand);
        tv_order_approval_color.setText(list.get(position).color);
        tv_order_approval_size.setText(list.get(position).size);
        tv_order_approval_qty.setText(list.get(position).qty);


        return convertView;
    }

    protected void publishResults(CharSequence constraint) {
        list = (ArrayList<OrderApprovalListModel>) constraint;
        notifyDataSetChanged();
    }
}
