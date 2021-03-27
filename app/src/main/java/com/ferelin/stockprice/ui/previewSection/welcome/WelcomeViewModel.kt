package com.ferelin.stockprice.ui.previewSection.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {

    private val mActionMoveToNextScreen = MutableStateFlow(false)
    val actionMoveToNextScreen: StateFlow<Boolean>
        get() = mActionMoveToNextScreen

    private val mMainTitleVisible = MutableLiveData(false)
    val mainTitleVisible: LiveData<Boolean>
        get() = mMainTitleVisible

    private val mHintChartSectionVisible = MutableLiveData(false)
    val hintChartSectionVisible: LiveData<Boolean>
        get() = mHintChartSectionVisible

    private val mHintFavouriteVisible = MutableLiveData(false)
    val hintFavouriteVisible: LiveData<Boolean>
        get() = mHintFavouriteVisible

    private val mHintApiLimitVisible = MutableLiveData(false)
    val hintApiLimitVisible: LiveData<Boolean>
        get() = mHintApiLimitVisible

    private val mBtnDoneVisible = MutableLiveData(false)
    val btnDoneVisible: LiveData<Boolean>
        get() = mBtnDoneVisible

    init {
        onStartScene()
    }

    fun onBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            mBtnDoneVisible.postValue(false)
            delay(200)
            mHintApiLimitVisible.postValue(false)
            delay(100)
            mHintChartSectionVisible.postValue(false)
            delay(100)
            mHintFavouriteVisible.postValue(false)
            delay(100)
            mMainTitleVisible.postValue(false)
            delay(100)
            mActionMoveToNextScreen.value = true
        }
    }

    private fun onStartScene() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            mMainTitleVisible.postValue(true)
            delay(1000)
            mHintFavouriteVisible.postValue(true)
            delay(1300)
            mHintChartSectionVisible.postValue(true)
            delay(1300)
            mHintApiLimitVisible.postValue(true)
            delay(200)
            mBtnDoneVisible.postValue(true)
        }
    }
}