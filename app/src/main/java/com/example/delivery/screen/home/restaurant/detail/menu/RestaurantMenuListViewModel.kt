package com.example.delivery.screen.home.restaurant.detail.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.model.restaurant.FoodModel
import com.example.delivery.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantMenuListViewModel(
    private val restaurantId: Long,
    private val foodEntityList: List<RestaurantFoodEntity>,
    private val restaurantFoodRepository: RestaurantFoodRepository
) : BaseViewModel() {

    val restaurantMenuListLiveData = MutableLiveData<List<FoodModel>>()

    val menuBasketLiveData = MutableLiveData<RestaurantFoodEntity>()

    val isClearNeedInBasketLiveData = MutableLiveData<Pair<Boolean, () -> Unit>>()

    override fun fetchData(): Job = viewModelScope.launch {
        restaurantMenuListLiveData.value = foodEntityList.map {
            FoodModel(
                id = it.hashCode().toLong(),
                title = it.title,
                description = it.description,
                price = it.price,
                imageUrl = it.imageUrl,
                restaurantId = restaurantId,
                foodId = it.id,
                restaurantTitle = it.restaurantTitle
            )
        }
    }

    /* 1-2-2-1. 메뉴 나열 : 클릭하면 장바구니에 담기
    * 장바구니에 메뉴 담기
    * anotherRestaurantMenuListInBasket에 다른 가게 음식이 있는지 담아서
    */
    fun insertMenuInBasket(foodModel: FoodModel) = viewModelScope.launch {
        val restaurantMenuListInBasket = restaurantFoodRepository.getFoodMenuListInBasket(restaurantId)
        val foodMenuEntity = foodModel.toEntity(restaurantMenuListInBasket.size)
        val anotherRestaurantMenuListInBasket =
            restaurantFoodRepository.getAllFoodMenuListInBasket().filter { it.restaurantId != restaurantId }
        if (anotherRestaurantMenuListInBasket.isNotEmpty()) {
            isClearNeedInBasketLiveData.value = Pair(true, { clearMenuAndInsertNewMenuInBasket(foodMenuEntity) })
        } else {
            restaurantFoodRepository.insertFoodMenuInBasket(foodMenuEntity)
            menuBasketLiveData.value = foodMenuEntity
        }
    }

    /* 1-2-2-1. 메뉴 나열 : 클릭하면 장바구니에 담기
    * 다른 가게 메뉴가 있으므로 장바구니를 비우고 새로 담는다.
    */
    private fun clearMenuAndInsertNewMenuInBasket(foodMenuEntity: RestaurantFoodEntity) = viewModelScope.launch {
        restaurantFoodRepository.clearFoodMenuListInBasket()
        restaurantFoodRepository.insertFoodMenuInBasket(foodMenuEntity)
        menuBasketLiveData.value = foodMenuEntity
    }

}
