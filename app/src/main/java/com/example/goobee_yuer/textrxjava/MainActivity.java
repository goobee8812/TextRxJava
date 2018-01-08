package com.example.goobee_yuer.textrxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private final String IMAGE_URL = "http://cdnq.duitang.com/uploads/item/201505/20/20150520102944_CiL3M.jpeg";


    private Button button;
    private ImageView imageView;
    private static OkHttpClient client;
    private Subscription bitmapSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) this.findViewById(R.id.button);
        imageView = (ImageView) this.findViewById(R.id.imageview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Bitmap> bitmapObservable = Observable.fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        return BitmapGet.getBitmap(IMAGE_URL);
                    }
                });
                bitmapSubscription = bitmapObservable
                        .subscribeOn(Schedulers.io())               //耗时操作线程
                        .observeOn(AndroidSchedulers.mainThread())  //
                        .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onCompleted() {

                            }
                            @Override
                            public void onError(Throwable e) {

                            }
                            @Override
                            public void onNext(Bitmap bitmap) {
                                Log.d("Tag", "onNext: ---Bitmap");
                                imageView.setImageBitmap(bitmap);
                                bitmapSubscription.unsubscribe();
                            }
                        });
            }
        });
    }
    private void getAndSetBitmap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(IMAGE_URL)
                            .build();
                    Response response = client.newCall(request).execute();
                    byte[] bytes =  response.body().bytes();
                    showBitmap(bytes);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showBitmap(final byte[] response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    private void showImage(Bitmap bitmap) {
        final Bitmap bitmap1 = bitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmapSubscription != null){
            bitmapSubscription.unsubscribe();
        }

    }
}
