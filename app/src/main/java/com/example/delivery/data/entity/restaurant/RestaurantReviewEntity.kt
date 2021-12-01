package com.example.delivery.data.entity.restaurant

import android.net.Uri
import com.example.delivery.data.entity.Entity

data class RestaurantReviewEntity(
    override val id: Long,
    val title: String,
    val description: String,
    val grade: Int,
    val images: List<Uri>? = null
): Entity
