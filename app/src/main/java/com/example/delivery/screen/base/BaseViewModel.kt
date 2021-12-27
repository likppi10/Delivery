package com.example.delivery.screen.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

    /*
    * 뷰모델의 틀로, 상태에 따라 진행할 fetchData을 정의하고, 해당 액티비티 및 프레그먼트가 가지는 상태에 따른 초기 fetchData 정의한다.
    */

abstract class  BaseViewModel: ViewModel() {

    protected var stateBundle: Bundle? = null

    open fun fetchData(): Job = viewModelScope.launch {  }

    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle

    }

}
