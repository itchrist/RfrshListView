package com.christ.measuredemo.listViewRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.christ.measuredemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2018/7/26.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "RefreshListView";
    private final Context context;

    private ProgressBar headerProgressBar;//正在加载进度条
    private TextView headerTitleTv;//标题文本框
    private TextView headerLastTimeTv;//最后刷新时间的文本框

    private View headerView;  //头布局视图
    private int headerViewHeight; //头布局的高度

    //三种刷新状态
    private static final int HEADER_STATE_PULL_TO_REFRESH = 1;  //下拉刷新状态
    private static final int HEADER_STATE_RELEASE_TO_REFRESH = 2; //释放刷新状态
    private static final int HEADER_STATE_REFRESHING = 3; //正在刷新状态
    private int currentState = HEADER_STATE_PULL_TO_REFRESH;//默认是下拉刷新状态

    /*********************************************************************************************/
    private float downY; //按下去的y的值
    //时间显示的格式
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
    /*********************************************************************************************/

    private View mFootView = null;
    private LayoutInflater mInflater = null;

    private int mTotalItemCount = 0;   // ListView的Item总数目
    private int mLastItemCount = 0;    //最后一个Item的位置
    private boolean mIsLoad = false;  //判断是否在加载

    public RefreshListView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }


    //1. 初始化视图
    private void initView() {
        //2. 初始化列表头布局
        initHeaderView();
        initFooterView();

    }

    //2. 初始化列表底布局
    private void initFooterView() {
        mInflater = LayoutInflater.from(context);
        mFootView = mInflater.inflate(R.layout.foot_layout, null, false);
        mFootView.setVisibility(View.GONE);
        addFooterView(mFootView);
        setOnScrollListener(this);
    }

    //2. 初始化列表头布局
    private void initHeaderView() {
        //2.1 装配列表头部布局 ，实例化 头布局xml文件

        headerView = View.inflate(getContext(), R.layout.head_view, null);
        //2.2 把头布局的根节点视图添加到ListView中
        this.addHeaderView(headerView);
        //2.3 通过父控件，引用子控件
        headerProgressBar = (ProgressBar) headerView.findViewById(R.id.refrush_pb);
        headerTitleTv = (TextView) headerView.findViewById(R.id.title_tv);
        headerLastTimeTv = (TextView) headerView.findViewById(R.id.lastTime_tv);

        //2.4 测量头布局控件
        headerView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        //取得头布局的高度

        headerViewHeight = headerView.getMeasuredHeight();

        //2.5 隐藏头布局
        hideHeaderView();

    }


    //3. 触摸事件，滑出头布局
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: //按下去
                downY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE://滑动
                float moveY = ev.getY();

                //计算y方向的偏移量
                float dy = moveY - downY;
                /**
                 * 当 向下滑动，即dy>0 ,且listview第一个可见的列表条目是一个 列表数据
                 */
                if (dy > 0 && getFirstVisiblePosition() == 0) {
                    //滑出头布局，更改headerView.setPadding(0，0,0,0)   -headerViewHeight=-50
                    int paddingTop = (int) (-headerViewHeight + dy);
                    showHeaderView(paddingTop);


                    //当paddingTop小于或者等于0，即头布局没有完全显示，则是下拉刷新状态
                    if (paddingTop <= 0 && currentState != HEADER_STATE_PULL_TO_REFRESH) {
                        currentState = HEADER_STATE_PULL_TO_REFRESH;
                        //更新头布局的界面
                        updateHeaderView();

                    }
                    //当paddingTop大于0，即头布局完全显示，则是释放刷新状态
                    else if (paddingTop > 0 && currentState != HEADER_STATE_RELEASE_TO_REFRESH) {
                        currentState = HEADER_STATE_RELEASE_TO_REFRESH;
                        updateHeaderView();
                    }
                    return true;//滑动事件消费完毕
                }


                break;
            case MotionEvent.ACTION_UP://抬起
                if (currentState == HEADER_STATE_PULL_TO_REFRESH) {
                    //隐藏头布局
                    hideHeaderView();
                } else if (currentState == HEADER_STATE_RELEASE_TO_REFRESH) {
                    currentState = HEADER_STATE_REFRESHING;
                    updateHeaderView();
                    //完全显示头布局
                    showHeaderView(0);
                    //4.4 刷新的业务处理 ，触发刷新的业务方法
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.refreshing();
                    }
                }

                break;

            default:
                break;
        }


        return super.onTouchEvent(ev);
    }


    //更新头布局的方法
    private void updateHeaderView() {
        switch (currentState) {
            case HEADER_STATE_PULL_TO_REFRESH://下拉刷新
                //todo
                headerTitleTv.setText("下拉刷新");
                headerProgressBar.setVisibility(View.INVISIBLE);

                break;
            case HEADER_STATE_RELEASE_TO_REFRESH://释放刷新
                System.out.println("HEADER_STATE_RELEASE_TO_REFRESH");
                headerProgressBar.setVisibility(View.INVISIBLE);
                headerTitleTv.setText("释放刷新");

                break;
            case HEADER_STATE_REFRESHING: //正在刷新
                headerTitleTv.setText("正在刷新...");
                headerProgressBar.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }


    //显示头部局
    private void showHeaderView(int paddingTop) {
        headerView.setPadding(0, paddingTop, 0, 0);
    }


    //隐藏头布局
    private void hideHeaderView() {
        headerView.setPadding(0, -headerViewHeight, 0, 0);
    }


    //刷新完成更新头布局界面方法
    public void setRefreshComplete() {
        //回到初始状态
        currentState = HEADER_STATE_PULL_TO_REFRESH;
        headerTitleTv.setText("下拉刷新");
        headerProgressBar.setVisibility(View.INVISIBLE);
        //更新最后刷新时间

        headerLastTimeTv.setText(dateFormat.format(new Date()));

        //隐藏头布局
        hideHeaderView();
    }

    /*****************************加载更多********************************************************/

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        Log.i(TAG, "onScrollStateChanged: " + mLastItemCount + "---" + scrollState);
        if (mLastItemCount == mTotalItemCount && scrollState == SCROLL_STATE_IDLE) {
            if (!mIsLoad) {
                mIsLoad = true;
                mFootView.setVisibility(View.VISIBLE);
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.loadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
        mLastItemCount = firstVisibleItem + visibleItemCount;
    }

    public void loadComplete() {
        mIsLoad = false;
        mFootView.setVisibility(View.GONE);
    }


    //4. 自定义接口 ，实现接口回调
    //4.1 定义一个接口
    public interface OnRefreshListener {
        void refreshing();

        void loadMore();
    }

    //4.2 声明接口对象
    private OnRefreshListener mOnRefreshListener;

    //4.3 对外提供一个传递接口对象的方法
    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }

}
