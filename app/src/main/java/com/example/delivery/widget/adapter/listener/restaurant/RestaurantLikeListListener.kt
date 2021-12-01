package com.example.delivery.widget.adapter.listener.restaurant

import com.example.delivery.model.restaurant.RestaurantModel

interface RestaurantLikeListListener: RestaurantListListener {

    fun onDislikeItem(model: RestaurantModel)

}
