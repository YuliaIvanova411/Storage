package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import java.net.ConnectException

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.RepositoryCallback<List<Post>>) {
        ApiService.api
            .getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }
                    val posts = response.body()

                    if (posts == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(ConnectException("There is no Internet at all"))
                }
            })
    }



    override fun saveAsync(post: Post, callback: PostRepository.RepositoryCallback<Post>) {
        ApiService.api.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()} \n${response.code()}"))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }
            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(ConnectException("There is no Internet at all"))
            }
        })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Unit>) {
        ApiService.api.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(value = Unit)

            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(ConnectException("There is no Internet at all"))
            }

        })
    }


    override fun likeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
        ApiService.api.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("There is no Internet at all"))
            }

        })
    }


    override fun dislikeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
        ApiService.api.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("There is no Internet at all"))
            }

        })
    }
    override fun getByIdAsync(id: Long,  callback: PostRepository.RepositoryCallback<Post>) {
        ApiService.api.getById(id).enqueue(object :Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("There is no Internet at all"))
            }

        })
    }

  }


//            }
//        val request: Request = Request.Builder()
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        return client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        val body = response.body?.string() ?: throw RuntimeException("body is null")
//                        callback.onSuccess(gson.fromJson(body, typeToken.type))
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//            })


//    override fun getByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
//        val request: Request = Request.Builder()
//            .url("${BASE_URL}/api/posts/$id")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, Post::class.java))
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//            })
//    }


//    override fun saveAsync(post: Post, callback: PostRepository.RepositoryCallback<Post>) {
//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("${BASE_URL}/api/posts")
//            .build()
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, Post::class.java))
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//
//            })
//    }


//    override fun likeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
//        val requestLike: Request = Request.Builder()
//            .post(EMPTY_REQUEST)
//            .url("${BASE_URL}/api/slow/posts/$id/likes")
//            .build()
//        client.newCall(requestLike)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, Post::class.java))
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//
//            })
//    }

//    override fun dislikeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
//        val request: Request = Request.Builder()
//            .delete()
//            .url("${BASE_URL}/api/slow/posts/$id/likes")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, Post::class.java))
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//            })
//
//    }
//
//    override fun removeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Unit>) {
//        val request: Request = Request.Builder()
//            .delete()
//            .url("${BASE_URL}/api/slow/posts/$id")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(Unit)
//                    } catch (e: Exception) {
//                        callback.onError()
//                    }
//                }
//
//            })
//    }


//    override fun shareByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
//
//    }
//}



