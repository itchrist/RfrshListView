package com.christ.measuredemo.customViewMatch;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.christ.measuredemo.R;

/**
 * Created by Administrator on 2018/7/27.
 * 自定义控件有被填充满
 */

public class View2TextView extends RelativeLayout {
    private TextView tvLeft;
    private TextView tvRight;

    public View2TextView(Context context) {
        super(context);
    }

    public View2TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO 需要这样填充
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_2textview, this, false);
        initView(inflate);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.View2TextView);
        String leftTv = ta.getString(R.styleable.View2TextView_leftTv);
        String rightTv = ta.getString(R.styleable.View2TextView_rightTv);
        ta.recycle();

        tvLeft.setText(leftTv);
        tvRight.setText(rightTv);

        this.addView(inflate);

    }

    private void initView(View inflate) {
        tvLeft = (TextView) inflate.findViewById(R.id.tv_left);
        tvRight = (TextView) inflate.findViewById(R.id.tv_right);
    }

}
