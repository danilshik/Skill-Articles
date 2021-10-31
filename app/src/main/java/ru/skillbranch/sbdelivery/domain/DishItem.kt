package ru.skillbranch.sbdelivery.domain

import java.io.Serializable

data class DishItem(
    val id: String,
    val image: String?,
    val price: String,
    val title: String,
    val isSale: Boolean = false,
    val isFavorite: Boolean = false,
) :Serializable