package com.christ.measuredemo.listViewRefresh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2018/7/26.
 */

public class RefreshAdapter extends BaseAdapter {
    Context context;
    private List<String> data;

    public RefreshAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() == 0 ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLUE);
        textView.setText(data.get(i));
        return textView;
    }

    public void setDataRefresh(List<String> data) {
        data = data;
        notifyDataSetChanged();
    }
}
