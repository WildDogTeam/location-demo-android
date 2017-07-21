package com.wilddog.testlocation.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wilddog.testlocation.CircleQueryActivy;

import java.util.List;

/**
 * Created by he on 2017/7/11.
 */

public class CircleAdapter extends ArrayAdapter<String> {
    private int pos=-1;

    public CircleAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public CircleAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CircleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }

    public CircleAdapter(CircleQueryActivy circleQueryActivy, int simple_list_item_1, List<String> keys) {
        super(circleQueryActivy, simple_list_item_1, keys);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView et = (TextView)view;
        ViewGroup.LayoutParams params = et.getLayoutParams();
        params.height=80;
        et.setLayoutParams(params);
        et.setTextSize(14);
        if(position==pos){
            et.setTextColor(Color.parseColor("#32af5a"));
        }else
            et.setTextColor(Color.parseColor("#333333"));
        et.setGravity(Gravity.CENTER);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        pos=-1;
        super.notifyDataSetChanged();

    }

    public void changeItemColor(int pos){
        this.pos = pos;
        super.notifyDataSetChanged();
    }
}
