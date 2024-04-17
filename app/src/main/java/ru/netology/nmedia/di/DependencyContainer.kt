//package ru.netology.nmedia.di
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.api.PostApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//import java.util.concurrent.TimeUnit
//
//
//
//class DependencyContainer (
//    private val context: Context
//){
//    companion object {
//        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context){
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance():DependencyContainer {
//            return instance!!
//            }
//        }
//
//
//    private val logging = HttpLoggingInterceptor().apply {
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor.Level.BODY
//        }
//    }
//    val appAuth = AppAuth(context)
//
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .addInterceptor { chain ->
//            appAuth.authState.value.token?.let { token ->
//                val newRequest = chain.request().newBuilder()
//                    .addHeader("Authorization", token)
//                    .build()
//                return@addInterceptor chain.proceed(newRequest)
//            }
//            chain.proceed(chain.request())
//        }
//        .addInterceptor(logging)
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(client)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//            private val appDb = Room.databaseBuilder(context, AppDb::class.java,"app.db")
//            .fallbackToDestructiveMigration()
//            .build()
//
//    val  apiService = retrofit.create<PostApiService>()
//
//    private val postDao = appDb.postDao()
//
//    val repository: PostRepository = PostRepositoryImpl(
//        postDao,
//        apiService,
//        )
//
//}
