package com.example.xz.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xz.HttpUtil.HttpUtil;
import com.example.xz.HttpUtil.Start;
import com.example.xz.zhihu.R;

import java.io.IOException;

/**
 * Created by xz on 2016/8/16.
 */

public class WelcomeActivity extends AppCompatActivity {
    private boolean flag=true;
    private TextView textView;
    private RelativeLayout layout;
    private Start start;
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        initUI();
        welcome();
         handler=new Handler(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Drawable startimage= (Drawable) msg.obj;
                layout.setBackground(startimage);
                textView.setText("3s后，自动跳过");
            }
        };

    }

    private void welcome() {
        new Thread(new Runnable() {
            int i=3;
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                //连接网络
                String jsonContent= HttpUtil.getJsonContent("http://news-at.zhihu.com/api/4/start-image/1080*1776");
                Log.i("welcome",jsonContent);
                start=HttpUtil.getStart(jsonContent);

                Bitmap bitmap= null;
                try {
                    bitmap = HttpUtil.getBitmap(start.getStartImageUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Drawable drawable=new BitmapDrawable(bitmap);
                Message message=handler.obtainMessage();
                message.obj=drawable;
                handler.sendMessage(message);


                //循环秒数
                do{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            textView.setText(i+"s后，自动跳过");
                        //    layout.setBackground(drawable);

                        }
                    });
                    if (!flag)
                        return;

                }while (i>0);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }).start();
    }

    private void initUI() {
        textView=(TextView)findViewById(R.id.start_text);
        layout=(RelativeLayout) findViewById(R.id.ll_welcome);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=false;
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }
}
