package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.dto.Post
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import java.util.concurrent.TimeUnit

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
interface PostApiService {

    @GET("posts")
    fun getAll(): Call<List<Post>>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Unit>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>
    @GET("posts/{id}")
    fun getById(@Path("id") id : Long) : Call<Post>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<Post>
}

object ApiService {
    val api: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}