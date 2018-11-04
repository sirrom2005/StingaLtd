package com.stingaltd.stingaltd.Interface;
import com.stingaltd.stingaltd.Models.Account;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IAccount {
    @GET("login/{username}/{password}")
    Call<Account> repo(@Path("username") String username, @Path("password") String password);
}
