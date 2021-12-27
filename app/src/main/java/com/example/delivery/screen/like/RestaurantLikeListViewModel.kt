package com.example.delivery.screen.like

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.restaurant.RestaurantEntity
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.model.CellType
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantLikeListViewModel(
    private val userRepository: UserRepository
): BaseViewModel() {

    val restaurantListLiveData = MutableLiveData<List<RestaurantModel>>()

    override fun fetchData(): Job = viewModelScope.launch {
        restaurantListLiveData.value = userRepository.getAllUserLikedRestaurant().map {
            RestaurantModel(
                id = it.id,
                type = CellType.LIKE_RESTAURANT_CELL,
                restaurantInfoId = it.restaurantInfoId,
                restaurantCategory = it.restaurantCategory,
                restaurantTitle = it.restaurantTitle,
                restaurantImageUrl = it.restaurantImageUrl,
                grade = it.grade,
                reviewCount = it.reviewCount,
                deliveryTimeRange = it.deliveryTimeRange,
                deliveryTipRange = it.deliveryTipRange,
                restaurantTelNumber = it.restaurantTelNumber,
            )
        }
    }

    /* 2. 찜 탭
        찜 가게 나열 : 클릭하면 해당 "가게 상세"로 이동
    *  찜 된 가게에서 하트를 다시 누르게 되면 찜 리스트에서 삭제되고 fetch를 한번 더해줌으로써
    *  즉각적인 적용을 할 수 있다.
    */
    fun dislikeRestaurant(restaurantEntity: RestaurantEntity) = viewModelScope.launch {
        userRepository.deleteUserLikedRestaurant(restaurantEntity.restaurantTitle)
        fetchData()
    }

}
