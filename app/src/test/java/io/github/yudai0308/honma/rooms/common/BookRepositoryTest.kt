package io.github.yudai0308.honma.rooms.common

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.github.yudai0308.honma.rooms.database.BookDatabase
import io.github.yudai0308.honma.rooms.entities.Author
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.utils.C
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class BookRepositoryTest {

    private lateinit var repository: BookRepository

    private lateinit var db: BookDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        repository = BookRepository(context)
        db = Room.databaseBuilder(
            context, BookDatabase::class.java, C.DB_NAME
        ).build()
    }

    @Test
    fun insertBookWithAuthors_レコードが追加できる() {
        val booksBefore = runBlocking { db.bookDao().loadAll() }
        val bookCountBefore = booksBefore.size
        val authorsBefore = runBlocking { db.authorDao().loadAll() }
        val authorCountBefore = authorsBefore.size

        val sampleBook = createSampleBook()
        val sampleAuthors = listOf(
            createSampleAuthor("author1"),
            createSampleAuthor("author2")
        )
        runBlocking { repository.insertBookWithAuthors(sampleBook, sampleAuthors) }
        val booksAfter = runBlocking { db.bookDao().loadAll() }
        val countAfter = booksAfter.size
        assertThat(countAfter).isEqualTo(bookCountBefore + 1)
        val authorsAfter = runBlocking { db.authorDao().loadAll() }
        val authorCountAfter = authorsAfter.size
        assertThat(authorCountAfter).isEqualTo(authorCountBefore + sampleAuthors.size)
    }

    @Test
    fun exists_レコードが存在しない場合falseを返却する() {
        val booksBefore = runBlocking { db.bookDao().loadAll() }
        val count = booksBefore.size
        assertThat(count).isEqualTo(0)
        val exists1 = repository.exists("abc")
        assertThat(exists1).isFalse()
    }

    @Test
    fun exists_レコードが存在する場合trueを返却する() {
        val book = createSampleBook(id = "abc")
        val author = createSampleAuthor()
        runBlocking { repository.insertBookWithAuthors(book, listOf(author)) }
        val exists2 = repository.exists("abc")
        assertThat(exists2).isTrue()
    }

    @Test
    fun delete_レコードが削除できる() {
        val book = createSampleBook(id = "abc")
        val author = createSampleAuthor("author")
        runBlocking { repository.insertBookWithAuthors(book, listOf(author)) }
        val exists1 = repository.exists("abc")
        runBlocking { db.authorDao().loadById(author.id) }
        assertThat(exists1).isTrue()
        runBlocking { repository.delete(book) }
        val exists2 = repository.exists("abc")
        assertThat(exists2).isFalse()
    }

    private fun createSampleBook(
        id: String = "abc",
        title: String = "title",
        description: String = "description",
        image: String = "image",
        infoLink: String = "infoLink",
        date: Long = Date().time
    ) = Book.create(id, title, description, image, infoLink, date)

    private fun createSampleAuthor(name: String = "author") = Author.create(name)
}