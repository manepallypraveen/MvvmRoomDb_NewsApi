package com.example.praveen.newsapp.network;

import com.example.praveen.newsapp.models.ArticleResponseWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("/v2/top-headlines")
    Call<ArticleResponseWrapper> getHeadlines(
            @Query("category") String category,
            @Query("country") String country,
            @Query("apikey") String apikey
    );

    enum Category {
        general("General");
        public final String title;
        Category(String title) {
            this.title = title;
        }
    }
}