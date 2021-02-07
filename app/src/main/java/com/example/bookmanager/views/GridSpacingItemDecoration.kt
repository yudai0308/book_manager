package com.example.bookmanager.views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.utils.ViewUtil

/**
 * グリッドリストの余白を調整するためのクラス。
 */
class GridSpacingItemDecoration(private val context: Context, private val itemPxWidth: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val displayWidth = ViewUtil.getDisplayWidth(context)
        val manager = parent.layoutManager as GridLayoutManager
        val spanCount = manager.spanCount
        val remainedWidthSpace = displayWidth - itemPxWidth * spanCount
        if (remainedWidthSpace < 0) {
            return
        }
        val eachWidthSpace = remainedWidthSpace / (spanCount + 1)

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.apply {
            left = if (column == 0) {
                eachWidthSpace
            } else {
                (eachWidthSpace / 2)
            }
            right = if (column == spanCount - 1) {
                eachWidthSpace
            } else {
                eachWidthSpace / 2
            }
            if (position < spanCount) {
                top = eachWidthSpace
            }
            bottom = eachWidthSpace
        }
    }
}
