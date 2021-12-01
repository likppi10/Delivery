package com.example.delivery.data.repository.restaurant

import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.restaurant.RestaurantEntity
import com.example.delivery.screen.home.restaurant.RestaurantCategory

interface RestaurantRepository {

    suspend fun getList(
        restaurantCategory: RestaurantCategory,
        locationLatLngEntity: LocationLatLngEntity
    ): List<RestaurantEntity>

}
