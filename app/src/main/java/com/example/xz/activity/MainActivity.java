package com.example.xz.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xz.HttpUtil.HttpUtil;
import com.example.xz.HttpUtil.Story;
import com.example.xz.zhihu.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    //侧滑菜单栏用到的控件
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};
    private ArrayAdapter arrayAdapter;
    private ListView lvLeftMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;
    private ArrayList<Story> stotiesList;
    private ArrayList<Story> topstoriesList;
    private Banner banner;
    private String[] images, titles;

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(MainActivity.this, "点击了search按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share:
                Toast.makeText(MainActivity.this, "点击了share按钮", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private static class ViewHolder {
        TextView title;
        ImageView image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.card_listView);
        listView.setEmptyView(findViewById(R.id.tv_empty));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        slidingView();

        //获取线程网络数据后在线程UI中进行更新
        final android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("handleMessage", "handleMessage执行了");
                super.handleMessage(msg);
                ArrayList[] arrayLists = (ArrayList[]) msg.obj;
                stotiesList = arrayLists[0];
                Log.i("storiesList", stotiesList.size() + "");
                topstoriesList = arrayLists[1];

                images = new String[topstoriesList.size()];
                titles = new String[topstoriesList.size()];
                for (int i = 0; i < topstoriesList.size(); i++) {
                    images[i] = topstoriesList.get(i).getImageUrl();
                    Log.i("images", topstoriesList.get(i).getImageUrl());
                    titles[i] = topstoriesList.get(i).getTitle();
                }


                //获取到storiesList后进行UI更新
                BaseAdapter adapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return stotiesList.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {


                        if (convertView == null) {
                            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item_card, null);
                        }
                        ViewHolder holder = null;
                        if (holder == null) {
                            holder = new ViewHolder();
                            holder.title = (TextView) convertView.findViewById(R.id.title);
                            holder.image = (ImageView) convertView.findViewById(R.id.image);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }


                        holder.title.setText(stotiesList.get(position).getTitle());
                        Log.i("title", stotiesList.get(position).getTitle());

                        holder.image.setImageBitmap(HttpUtil.bitmapList.get(position));
                        Log.i("position", position + "");


                        return convertView;
                    }


                };

                Log.i("MainActivity", "succeed");


                listView.addHeaderView(LayoutInflater.from(MainActivity.this).inflate(R.layout.advertisement, null));
                //轮播广告的实现
                banner = (Banner) findViewById(R.id.banner);
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                banner.setBannerTitle(titles);
                banner.setImages(images);
                banner.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent i = new Intent(MainActivity.this, ListActivity.class);
                        i.putExtra("shareUrl", topstoriesList.get(position - 1).getUrlId());
                        startActivity(i);
                    }
                });

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position--;

                        Log.i("onItemClick", stotiesList.get(position).getUrlId());
                        Intent i = new Intent(MainActivity.this, ListActivity.class);
                        i.putExtra("shareUrl", stotiesList.get(position).getUrlId());
                        startActivity(i);


                    }
                });
                //下拉刷新的实现
                swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.blue, R.color.green);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "您点击了刷新按钮", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 3000);
                    }
                });


            }
        };
        //开启线程读取网络数据
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("Runnable", "run方法执行了");
                String jsonContent = HttpUtil.getJsonContent("http://news-at.zhihu.com/api/4/news/latest");
                Log.i("MainActivity", jsonContent);
                ArrayList<Story> stotiesArrayList = HttpUtil.getStories(jsonContent);
                ArrayList<Story> topStoriesArray = HttpUtil.gettopStories(jsonContent);
                ArrayList[] arrayLists = new ArrayList[]{stotiesArrayList, topStoriesArray};
                Message message = handler.obtainMessage();

                message.obj = arrayLists;
                handler.sendMessage(message);
            }
        }
        );
        t.start();


    }
    //初始化各控件
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
    }

    //实现侧滑菜单栏
    private void slidingView() {
        findViews();
        toolbar.setTitle("知乎日报");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.action_settings) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
        toolbar.setOnMenuItemClickListener(MainActivity.this);
    }


}
