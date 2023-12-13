package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
        @Query("SELECT * FROM PostEntity ORDER BY id DESC")
        fun getAll(): LiveData<List<PostEntity>>
        @Query("SELECT COUNT(*) == 0 FROM PostEntity")
        suspend fun isEmpty() : Boolean
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

//        fun save(post: PostEntity) =
//                if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

        @Query("DELETE FROM PostEntity WHERE id = :id")
        suspend fun removeById(id: Long)

        @Query(
        """
           UPDATE PostEntity SET
               share = share + 1 WHERE id = :id;
        """
        )
        suspend fun shareById(id: Long)


    }
