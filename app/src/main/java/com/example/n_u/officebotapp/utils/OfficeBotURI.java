package com.example.n_u.officebotapp.utils;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class OfficeBotURI {
    private static final String BASE_SITE = "https://officebot-ahmedbutt2015.c9users.io/";
    private static final String BASE_URI = getBaseSite() + "api/";
    private static final String FACEBOOK_LOGIN = "https://officebot-ahmedbutt2015.c9users.io/api/facebook/login";
    private static final String FILE_URL_PRE_FIX = "https://officebot-ahmedbutt2015.c9users.io/storage/audio/";
//    private static final Gson GAIN_JSON = new GsonBuilder().create();
    private static final String REGISTER = "https://officebot-ahmedbutt2015.c9users.io/api/user/register";

    public static String getBaseSite() {
        return BASE_SITE;
    }

    private static String getBaseUri() {
        return BASE_URI;
    }

    public static String getFacebookLogin() {
        return FACEBOOK_LOGIN;
    }

    public static String getFileUrlPreFix() {
        return FILE_URL_PRE_FIX;
    }

    public static String getRegister() {
        return REGISTER;
    }

    @NonNull
    public static Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(getBaseUri())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create()))
                .build();
    }
}
