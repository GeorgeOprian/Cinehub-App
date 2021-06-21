package com.example.cinehub.API;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPIBuilder {

    private static ServerApiService serverAPI;
    private final static String BASE_URL = "http://192.168.0.115:8081";

    public static ServerApiService getInstance(){
        if(serverAPI == null){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            serverAPI = retrofit.create(ServerApiService.class);
        }
        return serverAPI;
    }
}
