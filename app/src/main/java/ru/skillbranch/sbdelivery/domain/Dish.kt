package ru.skillbranch.sbdelivery.domain

import java.io.Serializable


class Dish(
    val id: String,
    val title: String,
    val description: String,
    val image: String,
    val oldPrice: Int?,
    val price: Int,
    val rating: Float,
    val isFavorite: Boolean = false,
):Serializable