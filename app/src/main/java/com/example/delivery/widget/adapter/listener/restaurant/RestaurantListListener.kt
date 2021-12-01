package com.example.delivery.widget.adapter.listener.restaurant

import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.widget.adapter.listener.AdapterListener

interface RestaurantListListener: AdapterListener {

    fun onClickItem(model: RestaurantModel)

}
