package ru.netology.nmedia.api


import retrofit2.Response
import ru.netology.nmedia.dto.Post
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel


interface PostApiService {

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id")id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>
    @GET("posts/{id}")
    suspend fun getById(@Path("id") id : Long) : Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part file: MultipartBody.Part): Response<Media>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken): Response<Unit>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun login(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthModel>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun register(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthModel>


    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthModel>
}

//object ApiService {
//    val api: PostApiService by lazy {
//        retrofit.create(PostApiService::class.java)
//    }
//}