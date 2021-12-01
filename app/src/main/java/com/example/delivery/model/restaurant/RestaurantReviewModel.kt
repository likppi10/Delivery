package com.example.delivery.model.restaurant

import android.net.Uri
import com.example.delivery.model.CellType
import com.example.delivery.model.Model

data class RestaurantReviewModel(
    override val id: Long,
    override val type: CellType = CellType.REVIEW_CELL,
    val title: String,
    val description: String,
    val grade: Float,
    val thumbnailImageUri: Uri? = null
): Model(id, type)
