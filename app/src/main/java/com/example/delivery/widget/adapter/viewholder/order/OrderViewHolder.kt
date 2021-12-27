package com.example.delivery.widget.adapter.viewholder.order

import com.example.delivery.R
import com.example.delivery.databinding.ViewholderOrderBinding
import com.example.delivery.model.order.OrderModel
import com.example.delivery.screen.base.BaseViewModel
import com.example.delivery.util.provider.ResourcesProvider
import com.example.delivery.widget.adapter.listener.AdapterListener
import com.example.delivery.widget.adapter.listener.order.OrderListListener
import com.example.delivery.widget.adapter.viewholder.ModelViewHolder

class OrderViewHolder(
    private val binding: ViewholderOrderBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<OrderModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
        binding.orderContentText.text = ""
    }

    /* 3. 내정보 탭
    3-3. 주문 내역
    *  주문 내역의 정보 표시
    */
    override fun bindData(model: OrderModel) {
        super.bindData(model)
        with(binding) {
            orderTitleText.text = resourcesProvider.getString(R.string.order_history_title, model.orderId)

            val foodMenuList = model.foodMenuList

            foodMenuList
                .groupBy { it.title }
                .entries.forEach { (title, menuList) ->
                    val orderDataStr =
                        orderContentText.text.toString() + "메뉴 : $title | 가격 : ${menuList.first().price}원 X ${menuList.size}\n"
                    orderContentText.text = orderDataStr
                }
            orderContentText.text = orderContentText.text.trim()

            orderTotalPriceText.text =
                resourcesProvider.getString(R.string.price, foodMenuList.map { it.price }.reduce { total, price -> total + price })
        }
    }

    override fun bindViews(model: OrderModel, adapterListener: AdapterListener) {
        if (adapterListener is OrderListListener) {
            binding.root.setOnClickListener {
                adapterListener.writeRestaurantReview(model.orderId, model.restaurantTitle)
            }
        }
    }

}
