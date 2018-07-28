package com.christ.measuredemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.christ.measuredemo.listViewRefresh.RefreshAdapter;
import com.christ.measuredemo.listViewRefresh.RefreshListView;
import com.christ.measuredemo.swRefresh.ListViewLoadMore;
import com.christ.measuredemo.swRefresh.myAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListViewLoadMore lv;
    private ArrayList<String> datas;
    private int i = 0;
    private myAdapter adapter;
    private SwipeRefreshLayout sw;

    private LinearLayout mRootView;
    private LinearLayout mLinearLayout;
    private RefreshListView reListView;
    private List<String> data2;
    private RefreshAdapter refreshAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1.SwipeRefreshLayout+ListView 上下拉刷新
        initSwRefresh();

        //2.完全自定义ListView 上下拉刷新
        initRefresh();

        //动态摆放控件
        LayoutViewParams();
    }

    /**
     * 2.完全自定义ListView 上下拉刷新
     */
    private void initRefresh() {
        reListView = (RefreshListView) findViewById(R.id.reListView);
        data2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data2.add("数据" + i);
        }

        refreshAdapter = new RefreshAdapter(this, data2);
        reListView.setAdapter(refreshAdapter);

        reListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void refreshing() {
                final Random random = new Random();
                RefreshApp.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data2.add(0, "从头而降的新数据" + random.nextInt(100) + "号");
                        refreshAdapter.notifyDataSetChanged();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        reListView.setRefreshComplete();
                    }
                }, 1200);
            }

            @Override
            public void loadMore() {
                RefreshApp.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data2.add("新数据" + i);
                        i++;
                        adapter.notifyDataSetChanged();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        reListView.loadComplete();
                    }
                }, 2000);
            }
        });
    }

    /**
     * 1.SwipeRefreshLayout+ListView 上下拉刷新
     */
    private void initSwRefresh() {
        lv = (ListViewLoadMore) findViewById(R.id.listview);
        sw = (SwipeRefreshLayout) findViewById(R.id.sw);

        sw.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        sw.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                // TODO 获取数据
                final Random random = new Random();
                RefreshApp.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        datas.add(0, "从头而降的新数据" + random.nextInt(100) + "号");
                        adapter.notifyDataSetChanged();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        sw.setRefreshing(false);
                    }
                }, 1200);

                // System.out.println(Thread.currentThread().getName());

                // 这个不能写在外边，不然会直接收起来
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        lv.setOnLoadListener(new ListViewLoadMore.OnLoadListener() {
            @Override
            public void onLoad() {
                RefreshApp.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        datas.add("新数据" + i);
                        i++;
                        adapter.notifyDataSetChanged();
                        lv.loadComplete();
                    }
                }, 2000);

            }
        });


        datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add("数据" + i);
        }

        adapter = new myAdapter(this, datas);
        lv.setAdapter(adapter);
    }


    /**********************方法类*******************************************************************/


    /**
     * 动态摆放控件
     */
    private void LayoutViewParams() {
        mRootView = (LinearLayout) findViewById(R.id.mRootView);
        // 第一步，把 LinearLayout 添加到布局里面
        // 给LinearLayoutnew一个LinearLayout.LayoutParams，并且通过  etLayoutParams  让layout更新
        // LayoutParams可以从父窗体获得也可以自己new出来，这里我们采用自己new的方式
        mLinearLayout = new LinearLayout(MainActivity.this);
        mLinearLayout.setBackgroundColor(Color.parseColor("#0000ff"));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        mLinearLayout.setLayoutParams(layoutParams);
        mRootView.addView(mLinearLayout);

        // 第二步，把TextView 添加到 第一步的 LinearLayout 里面
        TextView textView = new TextView(MainActivity.this);
        textView.setText("代码动态添加控件");
        textView.setBackgroundColor(Color.parseColor("#ff0000"));
        textView.setGravity(Gravity.CENTER);
        mLinearLayout.addView(textView);

        // 第三步，把TextView获取对应父窗体类型的LayoutParams 并且设置参数更新layout
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(textView.getLayoutParams());
        textParams.setMargins(dip2px(this, 100),
                dip2px(this, 50),
                dip2px(this, 250),
                dip2px(this, 20));
        textParams.width = dip2px(MainActivity.this, 100);
        textParams.height = dip2px(MainActivity.this, 40);
//        textParams.gravity = Gravity.CENTER;  // 无效，这样并不能让文字在控件里居中
        textView.setLayoutParams(textParams);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}
