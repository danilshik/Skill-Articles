package ru.skillbranch.sbdelivery.domain

import java.io.Serializable

data class CategoryItem(
    val id: String,
    val title: String,
    val icon: String?,
    val order: Int,
    val parentId: String?
) : Serializable