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

package com.ferelin.stockprice.ui.messagesSection.relations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentRelationsBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.messagesSection.relations.adapter.RelationClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class RelationsFragment :
    BaseFragment<FragmentRelationsBinding, RelationsViewModel, RelationsViewController>(),
    RelationClickListener {

    override val mViewController = RelationsViewController()
    override val mViewModel: RelationsViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRelationsBinding
        get() = FragmentRelationsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewController.setArgumentsViewDependsOn(mViewModel.relationsAdapter)
        mViewModel.relationsAdapter.setOnClickListener(this)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectStateUserRegister()
        }
    }

    override fun onRelationClicked(position: Int) {
        // navigate
    }

    private suspend fun collectStateUserRegister() {
        mViewModel.userRegisterState
            .filter { it != null }
            .collect { Navigator.navigateToRegisterFragmentFromRelations(this) }
    }
}