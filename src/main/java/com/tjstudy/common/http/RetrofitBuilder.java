package com.tjstudy.common.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hdl.elog.ELog;
import com.tjstudy.common.base.BaseApp;
import com.tjstudy.common.base.Constants;
import com.tjstudy.common.http.response.BaseResponse;
import com.tjstudy.common.utils.SPUtils;
import com.tjstudy.common.utils.SSLSocketFactoryUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit网络工具管理类
 * <p>
 * Created by tjstudy on 2018/1/12.
 */

class RetrofitBuilder {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static RetrofitBuilder httpConfig;

    /**
     * 不对外提供直接创建对象的方法
     */
    private RetrofitBuilder() {
    }

    /**
     * 创建模式，new 出对象的示例，后续可以通过这个对象直接设置参数
     *
     * @return
     */
    static RetrofitBuilder builder() {
        httpConfig = new RetrofitBuilder();
        return httpConfig;
    }

    /**
     * 最后通过build方法，已经设置好的参数 对我们所需要的对象进行创建
     *
     * @return
     */
    Retrofit build() {
        //设置拦截器(添加公共参数，拦截服务器返回数据 错误数据的相关处理)
        File cacheFile = new File(BaseApp.instance.getCacheFileDir(), "cacheData");
        //设置缓存大小
        Cache cache = new Cache(cacheFile, httpConfig.cacheSize);//google建议放到这里
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder
                .connectTimeout(httpConfig.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(httpConfig.readTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(httpConfig.retry)//连接失败后是否重新连接
                .addNetworkInterceptor(new CommonCacheInterceptor())
                .addInterceptor(new CommonCacheInterceptor())
                .addInterceptor(new URLResponseInterceptor())
                .addInterceptor(new URLRequestInterceptor())
                .cache(cache)
                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(),
                        SSLSocketFactoryUtils.createTrustAllManager())//Https信任所有的证书
                .build();
        return new Retrofit.Builder()
                .baseUrl(httpConfig.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * 链接超时时间
     */
    private int connectTimeout = 15;
    /**
     * 读取数据超时时间
     */
    private int readTimeout = 15;

    /**
     * 连接失败是否重连
     */
    private boolean retry = true;
    /**
     * 是否缓存（缓存只有GET方式才有效）
     */
    private boolean cache = false;
    /**
     * 单位M，默认20M
     */
    private long cacheSize = 20;
    /**
     * 缓存时间：请求一次之后 在这个时间期限之内再次获取数据，将直接获取到缓存中的数据 默认单位 秒
     */
    private int cacheValidTime = 10;
    /**
     * 网络访问基础网址
     */
    private String baseUrl = HttpConstants.BASE_URL;
    /**
     * 公共参数
     */
    private Map<String, String> commonParams = new HashMap<>();

    public RetrofitBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RetrofitBuilder readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RetrofitBuilder retry(boolean retry) {
        this.retry = retry;
        return this;
    }

    public RetrofitBuilder cacheTime(int cacheValidTime) {
        this.cache = true;
        this.cacheValidTime = cacheValidTime;
        return this;
    }

    public RetrofitBuilder cacheSize(long cacheSize, int cacheValidTime) {
        this.cache = true;
        this.cacheSize = cacheSize;
        this.cacheValidTime = cacheValidTime;
        return this;
    }
    public RetrofitBuilder cache(boolean isCache){
        this.cache = isCache;
        return this;
    }

    public RetrofitBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitBuilder commonParams(Map<String, String> commonParams) {
        this.commonParams = commonParams;
        return this;
    }

    /**
     * 数据缓存拦截器:数据缓存，获取数据之后的10s 内获取缓存中的数据---可以避免多次刷新
     */
    private class CommonCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            //对服务器返回的数据 做缓存处理
            Request request = chain.request();
            Response response = chain.proceed(request);
            //缓存 在60s内的数据 在缓存中获取，超过60s还请求 则显示无数据了
            if (!httpConfig.cache) {
                return response;//不进行缓存
            }
            int maxAge = httpConfig.cacheValidTime;//10s
            return response.newBuilder()
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        }
    }

    /**
     * 访问服务器后返回数据 拦截器
     * 1：公共错误码处理
     * 2：测试完整接收到的数据
     * 3：读取header中的cookie
     */
    private static class URLResponseInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            //1-2 获取返回的整体内容，打印或者进行统一的错误码处理
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return response;
            }
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            //获取到服务器返回的数据 有一些数据需要全局处理
            String bodyString = buffer.clone().readString(charset);
            ELog.e("服务器数据=" + bodyString);
            try {
                Gson gson = new Gson();
                BaseResponse baseResponse = gson.fromJson(bodyString, BaseResponse.class);
                // TODO: 2018/1/12 统一处理服务器反馈数据
            } catch (Exception e) {
                e.printStackTrace();
            }

            //3、获取header 中的cookie数据
            String cookie = response.header("Cookie");
            ELog.e("cookie=" + cookie);
            //保存到本地 在其他网址访问时 添加这个cookie
            if (!TextUtils.isEmpty(cookie)) {
                SPUtils.setParam(BaseApp.instance.getApplicationContext(), Constants.SP_COOKIE, cookie);
            }
            return response;
        }
    }

    /**
     * 请求网址之前 的拦截操作
     * 1:添加公共参数
     * 2:添加Header
     */
    private static class URLRequestInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            //1:添加公共参数
            Map<String, String> params = httpConfig.commonParams;
            if (params != null) {
                if ("GET".equalsIgnoreCase(request.method())) {
                    //---------------------GET请求---------------------
                    HttpUrl.Builder httpUrl = request.url()
                            .newBuilder();
                    for (String key :
                            params.keySet()) {
                        httpUrl.addQueryParameter(key, params.get(key));
                    }
                    request = request.newBuilder().url(httpUrl.build()).build();
                } else {
                    //---------------------POST请求---------------------
                    if (request.body() instanceof FormBody) {
                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        FormBody formBody = (FormBody) request.body();

                        //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                        if (formBody != null) {//构造新的参数 需要添加
                            for (int i = 0; i < formBody.size(); i++) {
                                bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                            }
                        }
                        for (String key :
                                params.keySet()) {
                            bodyBuilder.addEncoded(key, params.get(key));
                        }
                        request = request.newBuilder().post(bodyBuilder.build()).build();
                    }
                }
            }
            //2:添加header
            String cookie = (String) SPUtils.getParam(BaseApp.instance.getApplicationContext(), Constants.SP_COOKIE, "");
            request = request.newBuilder().addHeader("Cookie", "JSESSIONID=" + cookie).build();
            return chain.proceed(request);
        }
    }
}