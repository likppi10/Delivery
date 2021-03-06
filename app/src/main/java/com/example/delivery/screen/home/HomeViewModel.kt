package com.example.delivery.screen.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.locaion.MapSearchInfoEntity
import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity
import com.example.delivery.data.repository.map.MapRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val restaurantFoodRepository: RestaurantFoodRepository
) : BaseViewModel() {

    companion object {
        const val MY_LOCATION_KEY = "MyLocation"
    }

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    val foodMenuBasketLiveData = MutableLiveData<List<RestaurantFoodEntity>>()

    fun loadReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity
    ) = viewModelScope.launch {
        homeStateLiveData.value = HomeState.Loading

        //UserLocation을 가져온다. 가져온게 없으면 현재 위치로
        val userLocation = userRepository.getUserLocation()
        val currentLocation = userLocation ?: locationLatLngEntity

        val addressInfo = mapRepository.getReverseGeoInformation(currentLocation)
        //addressInfo가 null이 아니면
        addressInfo?.let { info ->
            homeStateLiveData.value = HomeState.Success(
                MapSearchInfoEntity(
                    fullAddress = info.fullAddress ?: "주소 정보 없음",
                    name = info.buildingName ?: "빌딩정보 없음",
                    locationLatLng = currentLocation
                ),
                isLocationSame = userLocation == locationLatLngEntity
            )
        }
        //addressInfo null이면
        ?: kotlin.run {
            homeStateLiveData.value = HomeState.Error(
                messageId = R.string.can_not_load_address_info
            )
        }
    }

    /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
    * 상태가 성공이면(받아온 위치 정보가 있으면으로 해석 가능) 위치정보 반환
    */
    fun getMapSearchInfo(): MapSearchInfoEntity? {
        when (val data = homeStateLiveData.value) {
            is HomeState.Success -> {
                return data.mapSearchInfoEntity
            }
        }
        return null
    }

    // 홈에서 장바구니에 대한 데이터를 갖게하는 메서드
    fun checkMyBasket() = viewModelScope.launch {
        foodMenuBasketLiveData.value = restaurantFoodRepository.getAllFoodMenuListInBasket()
    }

}
