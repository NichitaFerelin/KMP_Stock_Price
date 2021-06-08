/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.stockprice.ui.bottomDrawerSection

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentBottomDrawerBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.onSlide.AlphaSlideAction
import com.ferelin.stockprice.utils.bottomDrawer.BottomSheetManager
import com.ferelin.stockprice.utils.bottomDrawer.OnSlideAction
import com.ferelin.stockprice.utils.themeColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable

class BottomDrawerFragment : Fragment() {

    private var mBinding: FragmentBottomDrawerBinding? = null

    private val mViewModel: BottomDrawerViewModel by viewModels()

    private var mBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    val bottomFragmentState: Int
        get() = mBottomSheetBehavior?.state ?: BottomSheetBehavior.STATE_HIDDEN

    private val mBottomSheetManager: BottomSheetManager by lazy {
        BottomSheetManager()
    }

    private val mBackgroundShapeDrawable: MaterialShapeDrawable by lazy {
        val context = requireContext()
        MaterialShapeDrawable(
            context,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(context.themeColor(R.attr.colorPrimary))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBottomDrawerBinding.inflate(inflater, container, false).also {
            if (savedInstanceState == null) {
                Navigator.navigateToMenuFragment(childFragmentManager)
            }
        }
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomDrawer()
        restoreScrimState()
        mBinding!!.viewScrim.setOnClickListener {
            closeDrawer()
        }
    }

    override fun onStop() {
        mViewModel.scrimVisibilityState = mBinding!!.viewScrim.visibility
        super.onStop()
    }

    override fun onDestroyView() {
        mBottomSheetBehavior = null
        mBinding = null
        super.onDestroyView()
    }

    fun onControlButtonPressed() {
        when (mBottomSheetBehavior!!.state) {
            BottomSheetBehavior.STATE_HIDDEN -> openHalfDrawer()
            BottomSheetBehavior.STATE_HALF_EXPANDED -> openDrawer()
            else -> closeDrawer()
        }
    }

    fun addOnSlideAction(action: OnSlideAction) {
        mBottomSheetManager.addOnSlideAction(action)
    }

    private fun initBottomDrawer() {
        with(mBinding!!) {
            mBottomSheetBehavior = BottomSheetBehavior.from(containerRoot).also {
                it.addBottomSheetCallback(mBottomSheetManager)
                it.state = BottomSheetBehavior.STATE_HIDDEN
            }
            addOnSlideAction(AlphaSlideAction(viewScrim))
            containerRoot.background = mBackgroundShapeDrawable
        }
    }

    private fun restoreScrimState() {
        if (mViewModel.scrimVisibilityState != View.GONE) {
            mBinding!!.viewScrim.visibility = mViewModel.scrimVisibilityState
            mBinding!!.viewScrim.alpha = 1F
        }
    }

    fun openDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }
    
    fun closeDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun openHalfDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }
}