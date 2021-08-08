package io.github.yudai0308.honma.test_utils

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewUtil {
    companion object {
        /**
         * RecyclerView の指定されたリストアイテム内にあるビューをマッチさせる。
         *
         * @param recyclerViewId RecyclerView の ID
         * @param targetViewId マッチさせるビューの ID
         * @param position リストアイテムのポジション
         * @return [TypeSafeMatcher]
         */
        fun withDescendantViewAtPosition(@IdRes recyclerViewId: Int, @IdRes targetViewId: Int, position: Int) =
            object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("指定された位置のリストアイテム内に、指定された ID のビューが存在すること")
                }

                override fun matchesSafely(view: View): Boolean {
                    val root = view.rootView
                    val recyclerView = root.findViewById<RecyclerView>(recyclerViewId) ?: return false
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return false
                    val targetView = viewHolder.itemView.findViewById<View>(targetViewId) ?: return false
                    return view == targetView
                }
            }

        /**
         * RecyclerViewActions.actionOnItemAtPosition() の第２引数として使用することを想定している。
         * RecyclerView のリストアイテム内のビューをクリックする。
         *
         * ```
         * onView(withId(R.id.recycler_view)).perform(
         *     RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
         *         0, clickDescendantViewWithId(R.id.my_button)
         *     )
         * )
         * ```
         */
        fun clickDescendantViewWithId(@IdRes id: Int) = object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.hasDescendant(ViewMatchers.withId(id))
            }

            override fun getDescription(): String {
                return "RecyclerView のリストアイテム内にあるビューをクリックする"
            }

            override fun perform(uiController: UiController, view: View) {
                view.findViewById<View>(id)?.also {
                    it.performClick()
                }

                val target = view.findViewById<View>(id) ?: return
                val action = GeneralClickAction(
                    Tap.SINGLE, GeneralLocation.VISIBLE_CENTER, Press.FINGER,
                    InputDevice.SOURCE_UNKNOWN, MotionEvent.BUTTON_PRIMARY)
                action.perform(uiController, target)
            }
        }
    }
}