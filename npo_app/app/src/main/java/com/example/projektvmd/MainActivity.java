package com.example.projektvmd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void sendToServer(View view) {
        String latitude = "243";
        //String postResponse = doPostRequest("http://www.roundsapp.com/post", latitude);
        RequestBody body = RequestBody.create(JSON, latitude);
        Request request = new Request.Builder()
                .url("http://www.roundsappbbbccc.com/post")
                .post(body)
                .build();

        //System.out.println(postResponse);

    }


}
