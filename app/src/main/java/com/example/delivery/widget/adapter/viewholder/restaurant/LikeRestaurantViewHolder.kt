package com.example.delivery.widget.adapter.viewholder.restaurant

import com.example.delivery.R
import com.example.delivery.databinding.ViewholderLikeRestaurantBinding
import com.example.delivery.extensions.clear
import com.example.delivery.extensions.load
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.screen.base.BaseViewModel
import com.example.delivery.util.provider.ResourcesProvider
import com.example.delivery.widget.adapter.listener.AdapterListener
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantLikeListListener
import com.example.delivery.widget.adapter.viewholder.ModelViewHolder

class LikeRestaurantViewHolder(
    private val binding: ViewholderLikeRestaurantBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<RestaurantModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = with(binding) {
        restaurantImage.clear()
    }

    /* 2. 찜 탭
        찜 가게 나열 : 클릭하면 해당 "가게 상세"로 이동
    *  찜한 가게 정보 표시
    */
    override fun bindData(model: RestaurantModel) {
        super.bindData(model)
        with(binding) {
            restaurantImage.load(model.restaurantImageUrl, 24f)
            restaurantTitleText.text = model.restaurantTitle
            gradeText.text = resourcesProvider.getString(R.string.grade_format, model.grade)
            reviewCountText.text = resourcesProvider.getString(R.string.review_count, model.reviewCount)
            val (minTime, maxTime) = model.deliveryTimeRange
            deliveryTimeText.text = resourcesProvider.getString(R.string.delivery_time, minTime, maxTime)

            val (minTip, maxTip) = model.deliveryTipRange
            deliveryTipText.text = resourcesProvider.getString(R.string.delivery_tip, minTip, maxTip)
        }
    }

    override fun bindViews(model: RestaurantModel, adapterListener: AdapterListener) = with(binding) {
        if (adapterListener is RestaurantLikeListListener) {
            root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
            likeImageButton.setOnClickListener {
                adapterListener.onDislikeItem(model)
            }
        }
    }

}
