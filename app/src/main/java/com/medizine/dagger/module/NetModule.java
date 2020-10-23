package com.medizine.dagger.module;

import android.app.Application;

import androidx.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {
    String mBaseUrl;

    public NetModule(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    private Cache provideHttpCache(Application application) {
        long httpCacheSize = 25 * 1024 * 1024; // 25 MiB
        File cache = new File(application.getCacheDir(), "http_cache");
        return new Cache(cache, httpCacheSize);
    }

    @NonNull
    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, context) -> new Date(jsonElement.getAsJsonPrimitive().getAsLong()));
        return gsonBuilder.create();
    }


    @NonNull
    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
//        builder.cache(provideHttpCache((Application) com.medizine.MedizineApp.getAppContext()));
//
//        final User user = StorageService.getInstance().getUser();
//        if (user != null) {
//            Interceptor interceptor = chain -> {
//                Request original = chain.request();
//
//                Request request = original.newBuilder()
//                        .header("userId", user.getId())
//                        .header("Authentication", Utils.getAuthenticationHeader())
//                        .header("appVersion", String.valueOf(Utils.getVersionCode()))
//                        .method(original.method(), original.body())
//                        .build();
//                Response response = chain.proceed(request);
//                if (response.code() == 401) {
//                    Utils.logOutUser();
//                }
//                return response;
//            };
//            builder.addInterceptor(interceptor);
//        }
//
//        if (BuildConfig.FLAVOR.equals("dev") || BuildConfig.DEBUG) {
//            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(httpLoggingInterceptor);
//        }
        return builder.build();
    }

    @NonNull
    @Provides
    @Singleton
    Retrofit provideRetrofit(@NonNull Gson gson, @NonNull OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}