package com.example.delivery.screen.home.restaurant.detail

import androidx.annotation.StringRes
import com.example.delivery.R

enum class RestaurantDetailCategory(
    @StringRes val categoryNameId: Int
) {

    MENU(R.string.menu), REVIEW(R.string.review)

}
