package com.e.belle.CustomExpandableListAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.belle.R;

import java.util.List;
import java.util.Map;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private Map<String,List<String>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, Map<String, List<String >> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }
    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }



    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);

     if(convertView == null) {
         LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = layoutInflater.inflate(R.layout.list_group,null);
     }

        TextView listTitleGroup = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleGroup.setTypeface(null, Typeface.BOLD);
        listTitleGroup.setText(listTitle);

        ImageView groupImage = (ImageView)convertView.findViewById(R.id.list_group_image);

        if(listTitle.equalsIgnoreCase("Approve Order")){
            groupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reorder_black_24dp));
        } else if(listTitle.equalsIgnoreCase("Activity")){
            groupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dashboard_pink_24dp));
        } else if(listTitle.equalsIgnoreCase("App Setting")){
            groupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_backspace_pink_32dp));
        }

        ImageView list_arrow_up_down = (ImageView) convertView.findViewById(R.id.list_arrow_up_down);

        if(isExpanded) {
            list_arrow_up_down.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_pink_24dp));
        } else {
            list_arrow_up_down.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_pink_24dp));
        }

        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String listItem = (String) getChild(listPosition, expandedListPosition);

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item,null);
        }
         TextView expandedListItem = convertView.findViewById(R.id.expandedListItem);
        expandedListItem.setText(listItem);
        return convertView;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
