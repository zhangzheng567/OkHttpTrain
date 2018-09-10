package com.example.anzh.okhttptrain;

/**
 * Created by anzh on 2018/9/5.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
