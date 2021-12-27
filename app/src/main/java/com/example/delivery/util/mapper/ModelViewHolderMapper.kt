package com.example.delivery.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.delivery.databinding.*
import com.example.delivery.model.CellType
import com.example.delivery.model.Model
import com.example.delivery.screen.base.BaseViewModel
import com.example.delivery.util.provider.ResourcesProvider
import com.example.delivery.widget.adapter.viewholder.ModelViewHolder
import com.example.delivery.widget.adapter.viewholder.food.FoodMenuViewHolder
import com.example.delivery.widget.adapter.viewholder.order.OrderMenuViewHolder
import com.example.delivery.widget.adapter.viewholder.order.OrderViewHolder
import com.example.delivery.widget.adapter.viewholder.restaurant.LikeRestaurantViewHolder
import com.example.delivery.widget.adapter.viewholder.restaurant.RestaurantViewHolder
import com.example.delivery.widget.adapter.viewholder.review.RestaurantReviewViewHolder

//반환값이 ModelViewHolder<M>이다.
//코틀린의 when은 switch랑 같다.
//as는 캐스팅을 하기 위한 함수이다.
//@Suppress("UNCHECKED_CAST")는 컴파일러 경고를 무시하기 위한 애노테이션이다.
object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M: Model> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
        resourcesProvider: ResourcesProvider
    ): ModelViewHolder<M> {

        // CellType에 따른 viewHolder 분기 기준은 CellType에 적어놓았다.
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = when (type) {
            CellType.RESTAURANT_CELL ->
                RestaurantViewHolder(
                    ViewholderRestaurantBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            CellType.LIKE_RESTAURANT_CELL ->
                LikeRestaurantViewHolder(
                    ViewholderLikeRestaurantBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            CellType.FOOD_CELL ->
                FoodMenuViewHolder(
                    ViewholderFoodMenuBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            CellType.ORDER_FOOD_CELL ->
                OrderMenuViewHolder(
                    ViewholderOrderMenuBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            CellType.REVIEW_CELL -> {
                RestaurantReviewViewHolder(
                    ViewholderRestaurantReviewBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            }
            CellType.ORDER_CELL -> {
                OrderViewHolder(
                    ViewholderOrderBinding.inflate(inflater, parent, false),
                    viewModel,
                    resourcesProvider
                )
            }
        }

        return viewHolder as ModelViewHolder<M>
    }

}
