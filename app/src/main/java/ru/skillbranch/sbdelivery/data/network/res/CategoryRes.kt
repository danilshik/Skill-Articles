package ru.skillbranch.sbdelivery.data.network.res

import com.squareup.moshi.Json
import ru.skillbranch.sbdelivery.data.db.entity.CategoryPersist


data class CategoryRes(
    @Json(name = "categoryId")
    val id: String,
    @Json(name = "name")
    val title: String,
    val order: Int,
    val icon: String?,
    val parent: String?,
    val active: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun CategoryRes.toCategoryPersist() =
    CategoryPersist(id, title, icon, order, parent, active, createdAt, updatedAt)