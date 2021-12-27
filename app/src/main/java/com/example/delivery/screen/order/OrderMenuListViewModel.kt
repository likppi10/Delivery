package com.example.delivery.screen.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.repository.order.DefaultOrderRepository
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.model.CellType
import com.example.delivery.model.restaurant.FoodModel
import com.example.delivery.screen.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderMenuListViewModel(
    private val restaurantFoodRepository: RestaurantFoodRepository,
    private val orderRepository: OrderRepository,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    val orderMenuState = MutableLiveData<OrderMenuState>(OrderMenuState.Uninitialized)

    // OrderMenuState의 foodModel과 다른 것은 같은 foodModel이지만 여기서는 32번째 줄 같이 ORDER_FOOD_CELL을 쓸 것이다.
    override fun fetchData(): Job = viewModelScope.launch {
        orderMenuState.value = OrderMenuState.Loading
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        orderMenuState.value = OrderMenuState.Success(
            foodMenuList.map {
                FoodModel(
                    id = it.hashCode().toLong(),
                    type = CellType.ORDER_FOOD_CELL,
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    restaurantId = it.restaurantId,
                    foodId = it.id,
                    restaurantTitle = it.restaurantTitle
                )
            }
        )
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    *  장바구니의 아이템이 클릭됐을 경우 장바구니에서 삭제된다.
    */
    fun removeOrderMenu(foodModel: FoodModel) = viewModelScope.launch {
        restaurantFoodRepository.removeFoodMenuListInBasket(foodModel.foodId)
        fetchData()
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    *  클릭하면 장바구니가 비워진다.
    */
    fun clearOrderMenu() = viewModelScope.launch {
        restaurantFoodRepository.clearFoodMenuListInBasket()
        fetchData()
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    * 장바구니에 뭔가 들어있다면,
    * 로그인이 되어있다면,
    * 주문 시도
    */
    fun orderMenu() = viewModelScope.launch {
        // 장바구니에 있는 데이터 가져오기
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        if (foodMenuList.isNotEmpty()) {
            // 음식점 아이디 값
            val restaurantId = foodMenuList.first().restaurantId
            val restaurantTitle = foodMenuList.first().restaurantTitle
            // 로그인 되어있다면 주문성공하면 바구니 비우기 실패하면 에러문 출력
            firebaseAuth.currentUser?.let { user ->
                when (val data = orderRepository.orderMenu(user.uid, restaurantId, foodMenuList, restaurantTitle)) {
                    is DefaultOrderRepository.Result.Success<*> -> {
                        restaurantFoodRepository.clearFoodMenuListInBasket()
                        orderMenuState.value = OrderMenuState.Order
                    }
                    is DefaultOrderRepository.Result.Error -> {
                        orderMenuState.value = OrderMenuState.Error(R.string.request_error, data.e)
                    }
                }
            } ?: kotlin.run {
                orderMenuState.value = OrderMenuState.Error(R.string.user_id_not_found, IllegalAccessException())
            }
        }
    }

}
