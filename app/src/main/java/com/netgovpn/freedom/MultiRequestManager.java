package com.netgovpn.freedom;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class MultiRequestManager {


    private OkHttpClient client;
    private Call call1;
    private Call call2;
    private boolean isCompleted = false;

    public interface Listener {
        void onSuccess(JSONObject json);

        void onFailure(Exception e);
    }

    public MultiRequestManager() {
        client = new OkHttpClient();
    }

    public void fetch(String url1, String url2, Listener listener) {
        Request request1 = new Request.Builder().url(url1).build();
        Request request2 = new Request.Builder().url(url2).build();

        call1 = client.newCall(request1);
        call2 = client.newCall(request2);

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                checkFailure(listener, e, call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isCompleted) return;

                String body = response.body() != null ? response.body().string() : "";
                try {
                    JSONObject json = new JSONObject(body);
                    isCompleted = true;

                    if (call == call1 && call2 != null) call2.cancel();
                    if (call == call2 && call1 != null) call1.cancel();

                    listener.onSuccess(json);
                } catch (Exception e) {
                    checkFailure(listener, e, call);
                }
            }
        };

        call1.enqueue(callback);
        call2.enqueue(callback);
    }

    private void checkFailure(Listener listener, Exception e, Call call) {
        if (call == call1 && (call2 == null || call2.isCanceled() || call2.isExecuted())) return;
        if (call == call2 && (call1 == null || call1.isCanceled() || call1.isExecuted())) return;

        if (!isCompleted) {
            isCompleted = true;
            listener.onFailure(e);
        }
    }

    public void cancelAll() {
        if (call1 != null) call1.cancel();
        if (call2 != null) call2.cancel();
    }
}