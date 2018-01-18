## Module common使用方式

> 项目开发的基础包，主要包括网络处理（Retroft2+RxJava）以及一些工具类，在应用的过程中继续对工具包进行扩展。

  
使用方式：
其他应用包，有需要的直接依赖当前包  

### **关于网络接口的添加**  
1. 根据整体的数据返回格式，修改BaseResponse.java文件
2. 修改HttpConstants.java中的BASE_URL
3. 接口定义，在APIService.java中添加响应的POST GET请求方式 @POST，查看retrofit定义格式，返回的具体数据，定义具体的实体，在BASEResponse实体的基础上进行处理。所有的网络访问都在HttpSend类中进行
4. 关于网络的设置，通过RetrofitBuilder来进行

### 示例
  	APIService.java
	 /**
     * 一，登录接口
     *
     * @param v版本号默认为"1.0"
     * @param username 用户名
     * @param pwd  密码
     * @return RxJava 对象
     */
    @FormUrlEncoded
    @POST("api/user/login")
    Observable<HttpBaseCallBack<User>> userLogin(@Field("v") String v,
											     @Field("username") String username,
											     @Field("password") String pwd);
	HttpSend.java
	private APIService getBaseApi() {
        return RetrofitBuilder.builder().build().create(APIService.class);
    }

	 /**
     * 催他
     *
     * @param goodsId
     * @param subscriber
     */
    public void subUrge(String goodsId, Subscriber<HttpBaseCallBack> subscriber) {
        getBaseApi().subGoodsUrge(HttpConstant.HTTP_V103, getToken(), goodsId)
	                .subscribeOn(Schedulers.newThread())
	                .observeOn(AndroidSchedulers.mainThread())
	                .subscribe(subscriber);
    }

最后的使用   
HttpSend.subUrge().....