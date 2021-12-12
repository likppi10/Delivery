package com.example.delivery.data

import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository

class TestRestaurantFoodRepository: RestaurantFoodRepository {

    private val foodMenuListInBasket = mutableListOf<RestaurantFoodEntity>()

    override suspend fun getFoods(
        restaurantId: Long,
        restaurantTitle: String
    ): List<RestaurantFoodEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFoodMenuListInBasket(): List<RestaurantFoodEntity> {
        return foodMenuListInBasket
    }

    override suspend fun getFoodMenuListInBasket(restaurantId: Long): List<RestaurantFoodEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFoodMenuInBasket(restaurantFoodEntity: RestaurantFoodEntity) {
       foodMenuListInBasket.add(restaurantFoodEntity)
    }

    override suspend fun removeFoodMenuListInBasket(foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearFoodMenuListInBasket() {
        foodMenuListInBasket.clear()
    }
}