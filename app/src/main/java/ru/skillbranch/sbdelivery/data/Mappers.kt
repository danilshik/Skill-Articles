package ru.skillbranch.sbdelivery.data

import ru.skillbranch.sbdelivery.data.db.entity.*
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.domain.CategoryItem
import ru.skillbranch.sbdelivery.domain.Dish
import ru.skillbranch.sbdelivery.domain.DishItem
import ru.skillbranch.sbdelivery.domain.CartItem

fun DishRes.toDishPersist(): DishPersist = DishPersist(
    id,
    name,
    description ?: "",
    image ?: "",
    oldPrice,
    price,
    rating ?: 0f,
    likes ?: 0,
    category,
    commentsCount ?: 0,
    active,
    createdAt,
    updatedAt
)

fun CartItemDV.toCartItem(): CartItem = CartItem(dishId, image, title, count, price)

fun DishPersist.toDishItem(): DishItem = DishItem(
    id = id,
    image = image,
    price = "$price",
    title = name,
    isSale = oldPrice?.let { true } ?: false
)

fun DishDV.toDish() = Dish(
    id = id,
    title = title,
    description = description,
    image = image,
    oldPrice = oldPrice,
    price = price,
    rating = rating,
    isFavorite = isFavorite,
)


fun DishItemDV.toDishItem() = DishItem(
    id = id,
    image = image,
    price = price,
    title = title,
    isSale = isSale,
    isFavorite = isFavorite
)

fun CategoryItemDV.toCategoryItem() = CategoryItem(
    id = id,
    title = title,
    icon = icon,
    order = order,
    parentId = parentId
)