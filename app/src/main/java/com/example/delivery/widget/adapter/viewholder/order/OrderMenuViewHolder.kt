package com.example.delivery.widget.adapter.viewholder.order

import com.example.delivery.R
import com.example.delivery.databinding.ViewholderOrderMenuBinding
import com.example.delivery.extensions.clear
import com.example.delivery.extensions.load
import com.example.delivery.model.restaurant.FoodModel
import com.example.delivery.screen.base.BaseViewModel
import com.example.delivery.util.provider.ResourcesProvider
import com.example.delivery.widget.adapter.listener.AdapterListener
import com.example.delivery.widget.adapter.listener.order.OrderMenuListListener
import com.example.delivery.widget.adapter.viewholder.ModelViewHolder
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class OrderMenuViewHolder(
    private val binding: ViewholderOrderMenuBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<FoodModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = with(binding) {
        foodImage.clear()
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    *  장바구니에 있는 메뉴의 정보 표시
    */
    override fun bindData(model: FoodModel) {
        super.bindData(model)
        with(binding) {
            foodImage.load(model.imageUrl, 24f, CenterCrop())
            foodTitleText.text = model.title
            foodDescriptionText.text = model.description
            priceText.text = resourcesProvider.getString(R.string.price, model.price)
        }
    }

    override fun bindViews(model: FoodModel, adapterListener: AdapterListener) {
        if (adapterListener is OrderMenuListListener) {
            binding.root.setOnClickListener {
                adapterListener.onRemoveItem(model)
            }
        }
    }

}
