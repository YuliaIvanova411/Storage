package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try{
        val response = when (loadType) {
            LoadType.REFRESH -> {
                val maxId = postRemoteKeyDao.max()
                if (maxId != null) {
                    postApiService.getAfter(maxId, state.config.pageSize)
                } else {
                    postApiService.getLatest(state.config.initialLoadSize)
                }
            }

            LoadType.APPEND -> {
                val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                postApiService.getBefore(id, state.config.pageSize)
            }

            LoadType.PREPEND ->  return MediatorResult.Success(true)

        }
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())

        appDb.withTransaction {
            when (loadType) {
                LoadType.REFRESH -> {
                    if (postDao.isEmpty()) {
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id,
                                ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id,
                                ),
                            )
                        )
                    } else {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                        )
                        )
                    }
                }

                LoadType.PREPEND -> Unit

                LoadType.APPEND -> {
                    PostRemoteKeyEntity(
                        PostRemoteKeyEntity.KeyType.BEFORE,
                        body.last().id,
                    )
                }

            }

            postDao.insert(body.map(PostEntity::fromDto))
        }
            return MediatorResult.Success(body.isEmpty())
    } catch (e: Exception) {
        return MediatorResult.Error(e)
        }
    }

}