package io.github.yudai0308.honma.pageobjects

import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.github.yudai0308.honma.R
import io.github.yudai0308.honma.testutils.RecyclerViewUtil
import org.hamcrest.core.AllOf
import org.hamcrest.core.Is.`is`

class BookSearchPage {
    fun search(keyword: String): BookSearchPage {
        onView(withResourceName("search_src_text")).perform(
            replaceText(keyword), closeSoftKeyboard()
        )

        onView(withResourceName("search_src_text")).perform(pressImeActionButton())

        return this
    }

    fun waitForSearchCompleted(device: UiDevice): BookSearchPage {
        val searchResultCond = Until.hasObject(
            By.res(
                "io.github.yudai0308.honma",
                "book_search_item_image_card"
            )
        )
        val waitSuccess = device.wait(searchResultCond, 3000L)
        assertThat(waitSuccess, `is`(true))

        return this
    }

    fun clickAddButtonAtFirstPosition(): BookSearchPage {
        onView(
            RecyclerViewUtil.withDescendantViewAtPosition(
                R.id.book_search_result_list, R.id.book_search_add_button, 0
            )
        ).perform(click())

        return this
    }

    fun goBackBookshelfPage(): BookshelfPage {
        onView(
            AllOf.allOf(
                withClassName(`is`(AppCompatImageButton::class.java.name)),
                withParent(withClassName(`is`(Toolbar::class.java.name)))
            )
        ).perform(click())

        return BookshelfPage()
    }
}