package io.github.yudai0308.honma.pageobjects

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.github.yudai0308.honma.R
import io.github.yudai0308.honma.testutils.RecyclerViewUtil
import org.hamcrest.core.Is.`is`

class BookshelfPage {
    fun goBookSearchPage(): BookSearchPage {
        onView(withId(R.id.fab_add_book)).perform(ViewActions.click())
        return BookSearchPage()
    }

    fun waitForBooksLoaded(device: UiDevice): BookshelfPage {
        val bookshelfCond = Until.hasObject(
            By.res(
                "io.github.yudai0308.honma",
                "bookshelf_item_cover"
            )
        )
        val waitSuccess = device.wait(bookshelfCond, 5000L)
        ViewMatchers.assertThat(waitSuccess, `is`(true))

        return this
    }

    fun assertNoBookExists(): BookshelfPage {
        val firstItem = getFirstItem() ?: throw Exception("onView() returns null")
        firstItem.check(doesNotExist())
        return this
    }

    fun assertAnyBookExists(): BookshelfPage {
        val firstItem = getFirstItem() ?: throw Exception("onView() returns null")
        firstItem.check(matches(isDisplayed()))
        return this
    }

    private fun getFirstItem(): ViewInteraction? {
        val firstItemMatcher = RecyclerViewUtil.withDescendantViewAtPosition(
            R.id.bookshelf_book_list, R.id.bookshelf_item_layout, 0
        )
        return onView(firstItemMatcher)
    }
}