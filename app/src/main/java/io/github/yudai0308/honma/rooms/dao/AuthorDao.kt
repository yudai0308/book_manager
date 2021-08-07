package io.github.yudai0308.honma.rooms.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.yudai0308.honma.rooms.entities.Author

@Dao
interface AuthorDao {
    @Insert
    suspend fun insert(author: Author): Long

    @Insert
    suspend fun insertAll(authors: List<Author>): List<Long>

    @Delete
    suspend fun delete(author: Author)

    @Query("SELECT * FROM authors")
    suspend fun loadAll(): List<Author>

    @Query("SELECT * FROM authors WHERE id = :id LIMIT 1")
    suspend fun loadById(id: Long): Author

    @Query("SELECT * FROM authors WHERE name = :name LIMIT 1")
    suspend fun loadByName(name: String): Author?
}
