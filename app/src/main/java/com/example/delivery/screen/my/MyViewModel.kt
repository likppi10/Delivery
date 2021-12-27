package com.example.delivery.screen.my

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.order.OrderEntity
import com.example.delivery.data.preference.AppPreferenceManager
import com.example.delivery.data.repository.order.DefaultOrderRepository
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.model.order.OrderModel
import com.example.delivery.screen.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 로그인 관리 할 sharedPreference, 주문 내역 불러와 담을 orderRepository,  로그아웃 하면 찜한 음식점 삭제를 위한 userRepository
class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
): BaseViewModel() {

    val myStateLiveData = MutableLiveData<MyState>(MyState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        myStateLiveData.value = MyState.Loading
        appPreferenceManager.getIdToken()?.let {
            myStateLiveData.value = MyState.Login(it)
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  로그인 후 사용자 토큰 정보를 저장
    */
    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            appPreferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  유저 정보 설정
    *  UID 기반으로 getAllOrderMenus로 모든 주문 불러와서  Success / Error 구분
    */
    @Suppress("UNCHECKED_CAST")
    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {
        firebaseUser?.let { user ->
            when (val orderMenusResult = orderRepository.getAllOrderMenus(user.uid)) {
                is DefaultOrderRepository.Result.Success<*> -> {
                    val orderList: List<OrderEntity> = orderMenusResult.data as List<OrderEntity>
                    Log.e("orders", orderMenusResult.data.toString())
                    myStateLiveData.value = MyState.Success.Registered(
                        user.displayName ?: "익명",
                        user.photoUrl,
                        orderList.map { order ->
                            OrderModel(
                                id = order.hashCode().toLong(),
                                orderId = order.id,
                                userId = order.userId,
                                restaurantId = order.restaurantId,
                                foodMenuList = order.foodMenuList,
                                restaurantTitle = order.restaurantTitle
                            )
                        }
                    )
                }
                is DefaultOrderRepository.Result.Error -> {
                    myStateLiveData.value = MyState.Error(
                        R.string.request_error,
                        orderMenusResult.e
                    )
                }
            }

        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  로그아웃
    */
    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            appPreferenceManager.removeIdToken()
        }
        userRepository.deleteALlUserLikedRestaurant()
        fetchData()
    }

}
