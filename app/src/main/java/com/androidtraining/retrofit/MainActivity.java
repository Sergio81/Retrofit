package com.androidtraining.retrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.androidtraining.retrofit.api.RandomAPI;
import com.androidtraining.retrofit.modules.RandomResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://randomuser.me/";
    private static final String TAG = "MainActivity_TAG";

    private Retrofit client;
    private RandomAPI randomAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = prepareRetrofitClient();
        randomAPI = client.create(RandomAPI.class);

        Log.d(TAG, "onCreate: Thread " + Thread.currentThread().getName());

        randomAPI.getRandomUser().enqueue(new Callback<RandomResponse>() {
            @Override
            public void onResponse(Call<RandomResponse> call, Response<RandomResponse> response) {
                if(response.isSuccessful()){
                    RandomResponse randomUser = response.body();
                    if(randomUser != null){
                        Log.d(TAG, "onResponse: seed " +
                        randomUser.getInfo().getSeed());
                        Log.d(TAG, "onResponse: Thread" +
                        Thread.currentThread().getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<RandomResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private Retrofit prepareRetrofitClient(){
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return client;
    }
}
