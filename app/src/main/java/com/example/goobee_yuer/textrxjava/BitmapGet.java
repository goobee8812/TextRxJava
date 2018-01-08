package com.example.goobee_yuer.textrxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Goobee_yuer on 2017/12/13.
 */

public class BitmapGet {

    private static OkHttpClient client;

    public static Bitmap getBitmap(final String url){
        Bitmap bitmap = null;
        try{
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            byte[] bytes =  response.body().bytes();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;

        }
    }
}
