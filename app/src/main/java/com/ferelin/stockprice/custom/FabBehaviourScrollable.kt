package com.ferelin.stockprice.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener

/**
 * [FabBehaviourScrollable] providing the behaviour for FAB that is localed at scrollable view.
 * - FAB will hide on the Scroll UP.
 * - FAB will show on the Scroll BOTTOM.
 *
 *  Used in xml. When changing the class name do not forget to change the XML file.
 */
class FabBehaviourScrollable(
    context: Context,
    attributeSet: AttributeSet
) : FloatingActionButton.Behavior() {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (dyConsumed <= 0 && child.visibility == View.VISIBLE) {
            child.hide(object : OnVisibilityChangedListener() {

                override fun onShown(fab: FloatingActionButton) {
                    super.onShown(fab)
                }

                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    fab.visibility = View.INVISIBLE
                }
            })
        } else if (dyConsumed > 0 && child.visibility != View.VISIBLE) {
            child.show()
        }
    }
}