package ru.netology.nmedia.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
        @Query("SELECT * FROM PostEntity ORDER BY id DESC")
        fun getAll(): Flow<List<PostEntity>>

        @Query("SELECT * FROM PostEntity ORDER BY id DESC")
        fun getPagingSource(): PagingSource<Int, PostEntity>

        @Query("SELECT COUNT(*) == 0 FROM PostEntity")
        suspend fun isEmpty() : Boolean

        @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
        fun getAllVisible(): Flow<List<PostEntity>>

        @Query("UPDATE PostEntity SET hidden = 0")
        fun readNew()

        @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 1")
        suspend fun newerCount(): Int

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(post: PostEntity)
        @Query(
                """
           UPDATE PostEntity SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id
           """
        )
        suspend fun likeById(id: Long)
        @Query("SELECT * FROM PostEntity WHERE id = :id")
        suspend fun getById(id: Long) : PostEntity

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(posts: List<PostEntity>)

        @Query("UPDATE PostEntity SET content = :text WHERE id = :id")
        fun updateContentById(id: Long, text: String)

        @Query("DELETE FROM PostEntity WHERE id = :id")
        suspend fun removeById(id: Long)

        @Query("DELETE FROM PostEntity")
        suspend fun clear()

        @Query(
        """
           UPDATE PostEntity SET
               share = share + 1 WHERE id = :id;
        """
        )
        suspend fun shareById(id: Long)
    }
