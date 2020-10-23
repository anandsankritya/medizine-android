package com.medizine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.R;
import com.medizine.model.enums.DrawerMenuItem;

import java.util.List;

public class DrawerMenuListAdapter extends ArrayAdapter<DrawerMenuItem> {

    private static final int VIEW_TYPE_SEPARATOR = 0;
    private static final int VIEW_TYPE_MENU_ITEM = 1;

    public DrawerMenuListAdapter(Context context, List<DrawerMenuItem> menuList) {
        super(context, 0, menuList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DrawerMenuItem menuItem = getItem(position);
        String menuTitle = null;
        if (menuItem != null) {
            menuTitle = menuItem.getLocaleString(getContext());
        }
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if (getItemViewType(position) == VIEW_TYPE_SEPARATOR) {
            convertView = layoutInflater.inflate(R.layout.drawer_list_seperator, parent, false);
            ((TextView) convertView.findViewById(R.id.tvMenuSeparator)).setText(menuTitle);
        } else {
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
            ((TextView) convertView.findViewById(R.id.tvMenuTitle)).setText(menuTitle);
            ((ImageView) convertView.findViewById(R.id.ivMenuIcon)).setImageResource(menuItem.getImageResource());
        }
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
//        if (DrawerMenuItem.VIEW_TYPE_SEPARATOR.equals(getItem(position))) {
//            return VIEW_TYPE_SEPARATOR;
//        }
        return VIEW_TYPE_MENU_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != VIEW_TYPE_SEPARATOR;
    }

}
