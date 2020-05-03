package com.example.bookmanager.rooms.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val image: String?,
    val comment: String?,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date
)
