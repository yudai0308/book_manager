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
    val comment: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    companion object {
        fun create(id: String, title: String, description: String, image: String): Book {
            val now = System.currentTimeMillis()
            return Book(id, title, description, image, "", now, now)
        }
    }
}
