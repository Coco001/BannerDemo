package com.coco.bannerdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Banner mBanner;
    private TPINewsData mNewsData = new TPINewsData();
    ArrayList<String> imagesUrl = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initEvecnt() {
        List<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData> news = mNewsData.data.news;
        int size = news.size();
        for (int i = 0; i < size; i++) {
            //设置图片地址构成的集合
            imagesUrl.add(news.get(i).listimage);
        }
        setBanner();
    }

    private void setBanner() {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());

        mBanner.setImages(imagesUrl);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Accordion);
        //设置标题集合（当banner样式有显示title时）
        String[] titles = new String[]{"砍价我最行", "人脉总动员", "人脉总动员","想不到你是这样的app", "疯狂购物节","砍价我最行", "人脉总动员", "想不到你是这样的app", "疯狂购物节","疯狂购物节"};
        mBanner.setBannerTitles(Arrays.asList(titles));
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.RIGHT);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    private void initData() {
        HttpUtil.sendOkHttpRequest("http://192.168.1.201:8080/zhbj/10007/list_1.json", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                parseDataWithFastJson(s);
            }
        });
    }

    private void initView() {
        mBanner = (Banner) findViewById(R.id.banner);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Picasso 加载图片简单用法
            Picasso.with(context).load((String) path).into(imageView);
        }
    }

    //使用fastjson解析数据
    private void parseDataWithFastJson(String s) {
        TPINewsData data = JSON.parseObject(s, TPINewsData.class);
        Message message = new Message();
        message.what = 0;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mNewsData = (TPINewsData) msg.obj;
                    initEvecnt();
                    break;
            }
            return true;
        }
    });

}
