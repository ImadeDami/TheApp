package com.theapp.zeathon;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NaMarkets {

    @FormUrlEncoded
    @POST("nasurvey/submit.php")
    //Call<Post> createPost(@Body Post post);
    Call<ResponseBody> createUser(
            @Field("firstName") String firstName,
            @Field("secondName") String secondName,
            @Field("gender") String gender,
            @Field("maritalStatus") String maritalStatus,
            @Field("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("nasurvey/login.php")
    Call<LoginResponse> userLogin(
            @Field("email")String email,
            @Field("password") String password);


}
