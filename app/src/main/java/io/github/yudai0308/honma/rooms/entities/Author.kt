package io.github.yudai0308.honma.rooms.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    indices = [Index(value = ["name"], unique = true)]
)
data class Author(
    @PrimaryKey(autoGenerate = true) val id: Long,
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
