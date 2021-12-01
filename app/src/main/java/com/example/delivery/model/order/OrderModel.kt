package com.example.delivery.model.order

import com.example.delivery.data.entity.order.OrderEntity
import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity
import com.example.delivery.model.CellType
import com.example.delivery.model.Model

data class OrderModel(
    override val id: Long,
    override val type: CellType = CellType.ORDER_CELL,
    val orderId: String,
    val userId: String,
    val restaurantId: Long,
    val foodMenuList: List<RestaurantFoodEntity>,
    val restaurantTitle: String
): Model(id, type) {

    fun toEntity() = OrderEntity(
        id = orderId,
        userId = userId,
        restaurantId = restaurantId,
        foodMenuList = foodMenuList,
        restaurantTitle = restaurantTitle
    )

}
