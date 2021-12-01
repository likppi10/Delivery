package com.example.delivery.widget.adapter.listener.restaurant

import com.example.delivery.model.restaurant.FoodModel
import com.example.delivery.widget.adapter.listener.AdapterListener

interface FoodMenuListListener: AdapterListener {

    fun onClickItem(model: FoodModel)

}
