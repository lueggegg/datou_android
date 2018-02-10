package com.example.luegg.oa.base.http;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.OaApplication;
import com.example.luegg.oa.base.SharedData;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;

/**
 * Created by luegg on 2017/12/11.
 */
public class CookieHandler {
    private static CookieHandler ourInstance = new CookieHandler();

    public static CookieHandler getInstance() {
        return ourInstance;
    }

    private HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    ClearableCookieJar cookieJar =
            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(OaApplication.instance));

    private CookieHandler() {
    }

    public ClearableCookieJar getCookieJar() {
        return cookieJar;
    }

    private void saveCookies() {
        SharedPreferences preferences = SharedData.getSharedPreferences(Constant.SHARED_KEY_COOKIES);
    }


    public void addCookie(String host, Cookie cookie) {
        List<Cookie> cookieList = cookieStore.get(host);
        if (cookieList == null) {
            cookieList = new LinkedList<>();
            cookieStore.put(host, cookieList);
        }
        Cookie oldOne = null;
        for (Cookie item : cookieList) {
            if (cookie.name().equals(item.name())) {
                oldOne = item;
                break;
            }
        }
        if (oldOne != null) {
            cookieList.remove(oldOne);
        }
        cookieList.add(cookie);
    }

    public List<Cookie> getCookies(String host) {
        List<Cookie> cookieList = cookieStore.get(host);
        return cookieList != null ? cookieList : new LinkedList<Cookie>();
    }
}
