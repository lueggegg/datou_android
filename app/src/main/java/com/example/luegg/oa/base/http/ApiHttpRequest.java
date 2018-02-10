package com.example.luegg.oa.base.http;

import android.os.Handler;
import android.os.Looper;

import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.OaApplication;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by luegg on 2017/12/1.
 */
public class ApiHttpRequest {
    public interface ObjectCallback{
        void onFailed();
        void onString(String response);
        void onStatus(int status);
        <T> void onObject(T object);
        <T> void onArray(List<T> objectList);
    }

    public static class ObjectCallbackAdapter implements ObjectCallback {
        public void onFailed() {}
        public void onString(String response) {}
        public void onStatus(int status) {}
        public <T> void onObject(T object) {}
        public <T> void onArray(List<T> objectList) {}
    }

    public static class CommonCallback implements Callback {
        private static final String TAG = "CommonCallback";
        protected String result;
        @Override
        public void onFailure(Call call, IOException e) {
            String url = call.request().url().toString();
            Logger.e(TAG,  url + " failed: " + e);
            CommonUtil.showToast("访问" + url + "出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                result = response.body().string();
            } catch (Exception e) {
                String url = call.request().url().toString();
                Logger.e(TAG, "访问" + url + ": 结果解析出错 " + e);
            }
        }
    }

    private boolean postToUi = true;
    private Handler handler;

    public ApiHttpRequest() {
        this(true);
    }

    public ApiHttpRequest(boolean ui) {
        postToUi = ui;
        if (postToUi) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    private void fetchStatus(Call call, final ObjectCallback objectCallback) {
        call.enqueue(new CommonCallback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                if (result != null && objectCallback != null) {
                    final ApiHttpResponse apiHttpResponse = new ApiHttpResponse();
                    apiHttpResponse.parseString(result);
                    if (postToUi) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                objectCallback.onStatus(apiHttpResponse.getStatus());
                            }
                        });
                    } else {
                        objectCallback.onStatus(apiHttpResponse.getStatus());
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (objectCallback != null) {
                    objectCallback.onFailed();
                }
            }

        });
    }

    private <T> void fetchObject(Call call, final Class<T> clazz, final ObjectCallback objectCallback) {
        call.enqueue(new CommonCallback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                if (result != null && objectCallback != null) {
                    final T object = ApiHttpResponse.parseToObject(result, clazz);
                    if (postToUi) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                objectCallback.onObject(object);
                            }
                        });
                    } else {
                        objectCallback.onObject(object);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (objectCallback != null) {
                    objectCallback.onFailed();
                }
            }
        });
    }

    private <T> void fetchArray(Call call, final Class<T> clazz, final ObjectCallback objectCallback) {
        call.enqueue(new CommonCallback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                if (result != null && objectCallback != null) {
                    final List<T> objectList = ApiHttpResponse.parseToArray(result, clazz);
                    if (postToUi) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                objectCallback.onArray(objectList);
                            }
                        });
                    } else {
                        objectCallback.onArray(objectList);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (objectCallback != null) {
                    objectCallback.onFailed();
                }
            }
        });
    }

    public void getStatus(String url, final ObjectCallback objectCallback) {
        Call call = buildGetCall(url);
        fetchStatus(call, objectCallback);
    }

    public <T> void getObject(String url, final Class<T> clazz, final ObjectCallback objectCallback) {
        Call call = buildGetCall(url);
        fetchObject(call, clazz, objectCallback);
    }

    public <T> void getArray(String url, final Class<T> clazz, final ObjectCallback objectCallback) {
        Call call = buildGetCall(url);
        fetchArray(call, clazz, objectCallback);
    }

    private OkHttpClient createHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(CookieHandler.getInstance().getCookieJar());
        return builder.build();
    }

    private Call buildGetCall(String url) {
        OkHttpClient client = createHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request);
    }

    public static class PostBuilder {
        private FormBody.Builder builder;
        private String _url;
        private boolean postToUi = true;

        public PostBuilder() {
            builder = new FormBody.Builder();
//            builder.add("all_allow", "1");
        }

        public PostBuilder add(String key, Object value) {
            builder.add(key, value.toString());
            return this;
        }

        public PostBuilder url(String url) {
            _url = url;
            return this;
        }

        public PostBuilder runOnUi(boolean ui) {
            postToUi = ui;
            return this;
        }

        public void executeForStatus(final ObjectCallback objectCallback) {
            ApiHttpRequest request = new ApiHttpRequest();
            request.postForStatus(_url, builder, objectCallback);
        }

        public <T> void executeForObject(final Class<T> clazz,  final ObjectCallback objectCallback) {
            ApiHttpRequest request = new ApiHttpRequest();
            request.postForObject(_url, builder, clazz, objectCallback);
        }

        public <T> void executeForArray(final Class<T> clazz,  final ObjectCallback objectCallback) {
            ApiHttpRequest request = new ApiHttpRequest();
            request.postForArray(_url, builder, clazz, objectCallback);
        }
    }

    private void postForStatus(String url, FormBody.Builder builder, final ObjectCallback objectCallback) {
        Call call = buildPostCall(url, builder);
        fetchStatus(call, objectCallback);
    }

    private  <T> void postForObject(String url, FormBody.Builder builder, final Class<T> clazz,  final ObjectCallback objectCallback) {
        Call call = buildPostCall(url, builder);
        fetchObject(call, clazz, objectCallback);
    }

    private  <T> void postForArray(String url, FormBody.Builder builder, final Class<T> clazz,  final ObjectCallback objectCallback) {
        Call call = buildPostCall(url, builder);
        fetchArray(call, clazz, objectCallback);
    }

    private Call buildPostCall(String url, FormBody.Builder builder) {
        OkHttpClient client = createHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        return client.newCall(request);
    }

    public static String getApiUrl(String path) {
        return Constant.HOST + "/api/" + path;
    }
}
