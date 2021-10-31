package ru.skillbranch.sbdelivery.data.network.res

import com.squareup.moshi.Json

data class DishRes(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val oldPrice: Int?,
    val price: Int,
    val rating: Float?,
    val likes: Int?,
    val category: String,
    val commentsCount: Int?,
    val active: Boolean,
    @Json(name = "isRecomendation")
    val isRecommended: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)


