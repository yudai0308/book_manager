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
    val status: Int,
    val comment: String,
    val startDate: Long,
    val finishDate: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    companion object {
        fun create(id: String, title: String, description: String, image: String): Book {
            val now = System.currentTimeMillis()
            return Book(id, title, description, image, 0, "", 0, 0, now, now)
        }
    }

    enum class Status(val code: Int) {
        PLANNING(0),
        READING(1),
        FINISHED(2)
    }
}
