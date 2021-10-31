package ru.skillbranch.sbdelivery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishPersist(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    val price: Int,
    val rating: Float,
    val likes: Int,
    val category: String,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@Entity(tableName = "dish_favorite")
data class DishLikedPersist(
    @PrimaryKey
    @ColumnInfo(name = "dish_id")
    val id: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = true,
)


@DatabaseView(
    """
        SELECT id, image, price, name AS title, old_price IS NOT NULL AS is_sale, likes AS like_count, rating,
        favorite.is_favorite AS is_favorite
        FROM dishes AS dish
        LEFT JOIN dish_favorite AS favorite ON favorite.dish_id = id
    """
)
data class DishItemDV(
    val id: String,
    val image: String?,
    val price: String,
    val title: String,
    val rating:Int = 0,
    @ColumnInfo(name = "like_count")
    val likeCount:Int = 0,
    @ColumnInfo(name = "is_sale")
    val isSale: Boolean = false,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
)


@DatabaseView(
    """
        SELECT id, image, price, name AS title, description, old_price, rating,
        favorite.is_favorite AS is_favorite
        FROM dishes AS dish
        LEFT JOIN dish_favorite AS favorite ON favorite.dish_id = id
    """
)
data class DishDV(
    val id: String,
    val title: String,
    val description: String,
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    val price: Int,
    val rating: Float,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
)