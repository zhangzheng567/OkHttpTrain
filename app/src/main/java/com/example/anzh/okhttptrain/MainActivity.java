package com.example.anzh.okhttptrain;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ThreadPoolExecutor threadWorkPool;
    private int threadCounter = 0;
    private Button sendRequestBtn;
    private TextView showResponseTv;
    String responseData;
    private final ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            threadCounter++;
            return new Thread(r, "worker_thread_" + threadCounter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initView() {
        sendRequestBtn = findViewById(R.id.send_request);
        showResponseTv = findViewById(R.id.show_response);
        sendRequestBtn.setOnClickListener(this);
    }

    private void initData() {
        threadWorkPool = new ThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(), 60, TimeUnit.SECONDS, new
                LinkedBlockingDeque<Runnable>(), threadFactory);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_request:
                useThreadPoolDoNetWork();
                break;
            default:
                break;
        }
    }

    private void useThreadPoolDoNetWork() {
        threadWorkPool.execute(new Runnable() {
            @Override
            public void run() {
                //getMethodHttpURLConection();
                postMethodOkhttp();

            }
        });
    }

    private void getMethodHttpURLConection() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(("http://www.baidu.com"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(80000);
            connection.setReadTimeout(80000);
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            setShowResponseTvContent(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                connection.disconnect();
            }
        }
    }


    private void postMethodHttpURLConection() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(("http://www.baidu.com"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(80000);
            connection.setReadTimeout(80000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes("username=admin&password=123456");
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            setShowResponseTvContent(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                connection.disconnect();
            }
        }
    }
    private void getMethodOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.baidu.com").build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            responseData = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setShowResponseTvContent(responseData);
    }

    private void postMethodOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("username", "admin").add("password", "123456").build();
        Request request = new Request.Builder().url("http://www.baidu.com").post(requestBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            responseData = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setShowResponseTvContent(responseData);
    }

    private int getCorePoolSize() {
        return Runtime.getRuntime().availableProcessors() * 2 + 1;
    }

    private int getMaxPoolSize() {
        return 50;
    }

    private void setShowResponseTvContent(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showResponseTv.setText(responseData);
            }
        });
    }

}
