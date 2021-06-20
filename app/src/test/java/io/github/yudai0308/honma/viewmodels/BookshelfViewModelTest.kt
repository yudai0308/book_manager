package io.github.yudai0308.honma.viewmodels

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.github.yudai0308.honma.rooms.database.BookDatabase
import io.github.yudai0308.honma.rooms.entities.Author
import io.github.yudai0308.honma.rooms.entities.AuthorBook
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.utils.C
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class BookshelfViewModelTest {

    private lateinit var db: BookDatabase

    lateinit var viewModel: BookshelfViewModel

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.databaseBuilder(
            context, BookDatabase::class.java, C.DB_NAME
        ).build()
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = BookshelfViewModel(application)
    }

    @Test
    fun sortByTitle_タイトル順でソートできる() {
        val books = getSampleBooks()
        val method = BookshelfViewModel::class.java.getDeclaredMethod(
            "sortByTitle", List::class.java, Boolean::class.java
        )
        method.isAccessible = true

        val sortedBooksAsc = method.invoke(viewModel, books, true) as List<Book>
        val sortedBookIdsAsc = sortedBooksAsc.map { it.id }
        val expectedIdsAsc = listOf("3", "2", "4", "8", "6", "1", "7", "5", "10", "9", "11", "12")
        assertThat(sortedBookIdsAsc).isEqualTo(expectedIdsAsc)

        val sortedBooksDesc = method.invoke(viewModel, books, false) as List<Book>
        val sortedBookIdsDesc = sortedBooksDesc.map { it.id }
        val expectedIdsDesc = expectedIdsAsc.reversed()
        assertThat(sortedBookIdsDesc).isEqualTo(expectedIdsDesc)
    }

    @Test
    fun sortByAuthor_著者名順でソートできる() {
        val books = getSampleBooks()
        books.forEach {
            runBlocking { db.bookDao().insert(it) }
        }
        val authors = listOf(
            Author(1, "ODA"), Author(2, "NATSUME")
        )
        runBlocking { db.authorDao().insertAll(authors) }
        val authorBookList = listOf(
            AuthorBook(1, 1, "1"),
            AuthorBook(2, 1, "2"),
            AuthorBook(3, 1, "3"),
            AuthorBook(4, 1, "4"),
            AuthorBook(5, 1, "5"),
            AuthorBook(6, 1, "6"),
            AuthorBook(7, 1, "7"),
            AuthorBook(8, 1, "8"),
            AuthorBook(9, 1, "9"),
            AuthorBook(10, 1, "10"),
            AuthorBook(11, 2, "11"),
            AuthorBook(12, 2, "12")
        )
        runBlocking { db.authorBookDao().insertAll(authorBookList) }

        val method = BookshelfViewModel::class.java.getDeclaredMethod(
            "sortByAuthor", List::class.java, Boolean::class.java
        )
        method.isAccessible = true

        val sortedBooksAsc = method.invoke(viewModel, books, true) as List<Book>
        val sortedBookIdsAsc = sortedBooksAsc.map { it.id }
        val expectedIdsAsc = listOf("11", "12", "3", "2", "4", "8", "6", "1", "7", "5", "10", "9")
        assertThat(sortedBookIdsAsc).isEqualTo(expectedIdsAsc)

        val sortedBooksDesc = method.invoke(viewModel, books, false) as List<Book>
        val sortedBookIdsDesc = sortedBooksDesc.map { it.id }
        val expectedIdsDesc = listOf("3", "2", "4", "8", "6", "1", "7", "5", "10", "9", "11", "12")
        assertThat(sortedBookIdsDesc).isEqualTo(expectedIdsDesc)
    }

    @Test
    fun sortByRating_評価順でソートできる() {
        val books = getSampleBooks()
        val method = BookshelfViewModel::class.java.getDeclaredMethod(
            "sortByRating", List::class.java, Boolean::class.java
        )
        method.isAccessible = true
        val sortedBooksAsc = method.invoke(viewModel, books, true) as List<Book>
        val sortedBookIdsAsc = sortedBooksAsc.map { it.id }
        val expectedIdsAsc = listOf(
            // 評価 2
            "2", "6",
            // 評価 3
            "3", "8",
            // 評価 4
            "4", "7", "9", "11",
            // 評価 5
            "1", "5", "10", "12"
        )
        assertThat(sortedBookIdsAsc).isEqualTo(expectedIdsAsc)

        val sortedBooksDesc = method.invoke(viewModel, books, false) as List<Book>
        val sortedBookIdsDesc = sortedBooksDesc.map { it.id }
        val expectedIdsDesc = listOf(
            // 評価 5
            "1", "5", "10", "12",
            // 評価 4
            "4", "7", "9", "11",
            // 評価 3
            "3", "8",
            // 評価 2
            "2", "6"
        )
        assertThat(sortedBookIdsDesc).isEqualTo(expectedIdsDesc)
    }

    @Test
    fun sortByPublishedDate_発刊日順でソートできる() {
        val books = getSampleBooks()
        val method = BookshelfViewModel::class.java.getDeclaredMethod(
            "sortByPublishedDate", List::class.java, Boolean::class.java
        )
        method.isAccessible = true
        val sortedBooksAsc = method.invoke(viewModel, books, true) as List<Book>
        val sortedBookIdsAsc = sortedBooksAsc.map { it.id }
        val expectedIdsAsc = listOf("11", "12", "3", "2", "4", "8", "6", "1", "7", "5", "10", "9")
        assertThat(sortedBookIdsAsc).isEqualTo(expectedIdsAsc)

        val sortedBooksDesc = method.invoke(viewModel, books, false) as List<Book>
        val sortedBookIdsDesc = sortedBooksDesc.map { it.id }
        val expectedIds = expectedIdsAsc.reversed()
        assertThat(sortedBookIdsDesc).isEqualTo(expectedIds)
    }

    private fun getSampleBooks(): List<Book> {
        return listOf(
            Book("1", "ONE PIECE 16", "", "", 5, 0, "", "ONE PIECE", "", 160000, 0, 0, 0, 0),
            Book("2", "ONE PIECE 2", "", "", 2, 0, "", "ONE PIECE", "", 20000, 0, 0, 0, 0),
            Book("3", "ONE PIECE 1", "", "", 3, 0, "", "ONE PIECE", "", 10000, 0, 0, 0, 0),
            Book("4", "ONE PIECE 8", "", "", 4, 0, "", "ONE PIECE", "", 80000, 0, 0, 0, 0),
            Book("5", "ONE PIECE 28", "", "", 5, 0, "", "ONE PIECE", "", 280000, 0, 0, 0, 0),
            Book("6", "ONE PIECE 15", "", "", 2, 0, "", "ONE PIECE", "", 150000, 0, 0, 0, 0),
            Book("7", "ONE PIECE 23", "", "", 4, 0, "", "ONE PIECE", "", 230000, 0, 0, 0, 0),
            Book("8", "ONE PIECE 11", "", "", 3, 0, "", "ONE PIECE", "", 110000, 0, 0, 0, 0),
            Book("9", "ONE PIECE 45", "", "", 4, 0, "", "ONE PIECE", "", 450000, 0, 0, 0, 0),
            Book("10", "ONE PIECE 33", "", "", 5, 0, "", "ONE PIECE", "", 330000, 0, 0, 0, 0),
            Book("11", "こころ", "", "", 4, 0, "", "こころ", "", 5000, 0, 0, 0, 0),
            Book("12", "吾輩は猫である", "", "", 5, 0, "", "吾輩は猫である", "", 8000, 0, 0, 0, 0)
        )
    }
}