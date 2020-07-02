package com.example.bookmanager.rooms.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    indices = [Index(value = ["name"], unique = true)]
)
data class Author(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
) {
    companion object {
        fun create(name: String): Author {
            return Author(0, name)
        }

        fun createAll(names: List<String>): List<Author> {
            return names.map { Author(0, it) }
        }
    }
}
