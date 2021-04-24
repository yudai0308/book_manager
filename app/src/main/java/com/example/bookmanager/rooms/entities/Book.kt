package com.example.bookmanager.rooms.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val image: String,
    val rating: Int,
    val status: Int,
    val comment: String,
    @ColumnInfo(name = "published_date") val publishedDate: Long,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "finished_at") val finishedAt: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    companion object {
        fun create(id: String, title: String, description: String, image: String, publishedDate: Long): Book {
            val now = System.currentTimeMillis()
            return Book(id, title, description, image, 0, 0, "", publishedDate, 0, 0, now, now)
        }
    }

    enum class Status(val code: Int) {
        WANT_TO_READ(0),
        READING(1),
        FINISHED(2)
    }

    enum class Column {
        TITLE,
        AUTHOR,
        CREATED_AT,
        STARTED_AT,
        FINISHED_AT
    }
}
