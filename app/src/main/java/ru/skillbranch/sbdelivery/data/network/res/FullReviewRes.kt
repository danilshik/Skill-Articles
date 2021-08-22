package ru.skillbranch.sbdelivery.data.network.res

import java.io.Serializable

data class FullReviewRes(
    val dishId: String,
    val author: String,
    val date: String,
    val order: Int,
    val rating: Int,
    val text: String,
    val active: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
) : Serializable