package io.github.yudai0308.honma.rooms.entities

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
    @ColumnInfo(name = "series_name") val seriesName: String,
    @ColumnInfo(name = "info_link") val infoLink: String,
    @ColumnInfo(name = "published_date") val publishedDate: Long,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "finished_at") val finishedAt: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    companion object {
        fun create(id: String, title: String, description: String, image: String, infoLink: String, publishedDate: Long): Book {
            val now = System.currentTimeMillis()
            val seriesName = getSeriesName(title)
            return Book(id, title, description, image, 0, 0, "", seriesName, infoLink, publishedDate, 0, 0, now, now)
        }

        private fun getSeriesName(string: String): String {
            val stringArray = string.split("").filter { it.isNotBlank() }
            val removedStringArray = stringArray.mapIndexed { i, str ->
                // タイトルの最初の１文字が数字だったら削除しない。
                if (i == 0 || str.toIntOrNull() == null) {
                    str
                } else {
                    ""
                }
            }
            var removedString = ""
            removedStringArray.forEach { removedString += it }
            return removedString.trim()
        }
    }

    enum class Status(val code: Int) {
        WANT_TO_READ(0),
        READING(1),
        FINISHED(2)
    }

    enum class Column(val code: Int) {
        TITLE(0),
        AUTHOR(1),
        CREATED_AT(2),
        RATING(3);

        companion object {
            fun getByCode(code: Int): Column? {
                return when (code) {
                    TITLE.code -> TITLE
                    AUTHOR.code -> AUTHOR
                    CREATED_AT.code -> CREATED_AT
                    RATING.code -> RATING
                    else -> null
                }
            }
        }
    }
}
