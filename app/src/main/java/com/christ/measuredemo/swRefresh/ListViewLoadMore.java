package com.christ.measuredemo.swRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.christ.measuredemo.R;

/**
 * Created by Administrator on 2018/7/27.
 */

public class ListViewLoadMore extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "ListViewLoadMore";
    private View mFootView = null;
    private LayoutInflater mInflater = null;

    private int mTotalItemCount = 0;   // ListView的Item总数目
    private int mLastItemCount = 0;    //最后一个Item的位置
    private boolean mIsLoad = false;  //判断是否在加载

    public ListViewLoadMore(Context context) {
        super(context);
    }

    public ListViewLoadMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ListViewLoadMore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        mFootView = mInflater.inflate(R.layout.foot_layout, null, false);
        mFootView.setVisibility(View.GONE);
        addFooterView(mFootView);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.i(TAG, "onScrollStateChanged: " + mLastItemCount + "---" + scrollState);
        if (mLastItemCount == mTotalItemCount && scrollState == SCROLL_STATE_IDLE) {
            if (!mIsLoad) {
                mIsLoad = true;
                mFootView.setVisibility(View.VISIBLE);
                if (mListener != null) {
                    mListener.onLoad();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
        mLastItemCount = firstVisibleItem + visibleItemCount;
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mListener = listener;
    }

    public OnLoadListener mListener;

    public interface OnLoadListener {
        void onLoad();
    }

    public void loadComplete() {
        mIsLoad = false;
        mFootView.setVisibility(View.GONE);
    }
}
