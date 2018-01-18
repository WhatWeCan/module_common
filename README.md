## Module common快速使用

概述：  
1. Retrofit+RxJava网络访问  
2. 工具类   
3. 公共组件  

### 工具类
- APPUtils
- NetUtils
- SPUtils
- ToastUtils
	
### 公共组件
- CusTitleBar标题栏
	设置左右图片以及标题	  

		<com.tjstudy.common.widget.CusTitleBar
		    android:id="@+id/title_bar"
		    android:layout_width="match_parent"
		    android:layout_height="wrap_content"
		    app:titleBarLeftImage="@drawable/ease_mm_title_back" /> 
	
		titleBar = (EaseTitleBar) getView().findViewById(R.id.title_bar);
		titleBar.setTitle("张建国");
		titleBar.setRightImageResource(R.drawable.ease_mm_title_remove);
### Retrofit+RxJava使用
网络相关代码都在http包，根据实际需求，参考使用步骤：   
 
1.  修改网络访问返回格式，**BaseResponse.java**(数据返回一般都会有一个格式)    

	对应类：   
 
		public class BaseResponse<T> {

		    /**
		     * status : true
		     * msg : 获取成功
		     * timestamp : 2017-08-08 22:36:06
		     * data : {}
		     */
		
		    private boolean status;
		    private String msg;
		    private String timestamp;
		    @SerializedName("data")
		    private T t;
		
		    public boolean isStatus() {
		        return status;
		    }
		
		    public void setStatus(boolean status) {
		        this.status = status;
		    }
		
		    public String getMsg() {
		        return msg;
		    }
		
		    public void setMsg(String msg) {
		        this.msg = msg;
		    }
		
		    public String getTimestamp() {
		        return timestamp;
		    }
		
		    public void setTimestamp(String timestamp) {
		        this.timestamp = timestamp;
		    }
		
		    public T getT() {
		        return t;
		    }
		
		    public void setT(T t) {
		        this.t = t;
		    }
		}  

2. 添加基础网址(HttpConstants BaseUrl)  
3. 网络配置（HttpSend）  
	
	通过RetrofitBuilder.builder()设置  

		APIService apiService = RetrofitBuilder.builder()
	                .baseUrl("")//网络地址前缀
	                .connectTimeout(12)//连接超时时间 单位/秒
	                .readTimeout(12)//读取时间 单位/秒
	                .retry(false)//连接失败是否重连 默认进行重连
	                .cache(true)//默认不缓存 缓存默认值，大小20M 时间20s,缓存的设置只有GET请求有效
	                .cacheSize(40, 60)//缓存大小单位;缓存时间单位秒
	                .cacheTime(60)//缓存时间
	                .commonParams(new HashMap<String, String>())//设置公共参数 例如设置接口版本号
	                .build()
	                .create(APIService.class);

4. 添加网络接口，APIService.java 和 HttpSend.java  
	APIService.java  ，Retrofit接口请求方式，详细参考官网**http://square.github.io/retrofit/**。常用方式记录：   

  - GET请求    
	
			@GET("api/user/login")
	    	Observable<HttpBaseCallBack<User>> userLogin1(@Query("v") String v,
                                                 @Query("username") String username, 
  - post表单提交  
  		

			/**
		     * 一，登录接口
		     *
		     * @param v        版本号        默认为"1.0"
		     * @param username 用户名
		     * @param pwd      密码
		     * @return RxJava 对象
		     */
		    @FormUrlEncoded
		    @POST("api/user/login")
		    Observable<BaseResponse<User>> userLogin(@Field("v") String v,
                                                 @Field("username") String username,
                                                 @Field("password") String pwd);
		
			HttpSend.java  

			/**
		     * 登录
		     *
		     * @param username   用户名
		     * @param pwd        密码
		     * @param subscriber
		     */
		    public void login(String username, String pwd, Subscriber<BaseResponse<User>> subscriber) {
		        RetrofitUtils.LoginHttp(HttpConstant.BASE_TEST_URL, MyApp.mContext)
		                .create(APIService.class)
		                .userLogin(HttpConstant.HTTP_V, username, pwd)
		                .subscribeOn(Schedulers.newThread())
		                .observeOn(AndroidSchedulers.mainThread())
		                .subscribe(subscriber);
		    }  

	- 图片上传  
	
			/**
		     * 上传图片
		     *
		     * @param v     版本号
		     * @param token
		     * @param image
		     * @return
		     */
		    @Multipart
		    @POST("api/upload/upimage")
		    Observable<ImageUploadCallback> upImage(@Part("v") RequestBody v,
		                                            @Part("token") RequestBody token,
		                                            @Part("image\"; filename=\"image.jpg") RequestBody image);
			RequestBody格式示例（HttpSend）：    

		    RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/png"), new File(picPath));
	        RequestBody v = RequestBody.create(MediaType.parse("multipart/form-data"), HttpConstant.HTTP_V);
	        RequestBody tokenRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), getToken());
	        cookieService.upImage(v, tokenRequestBody, imageRequestBody)
	                .subscribeOn(Schedulers.newThread())
	                .observeOn(AndroidSchedulers.mainThread())
	                .subscribe(subscriber);  
	