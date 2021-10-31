package ru.skillbranch.sbdelivery.screens.menu.data

import ru.skillbranch.sbdelivery.domain.CategoryItem
import java.io.Serializable

sealed class CategoryUiState : Serializable {
    object Loading : CategoryUiState()
    object Empty : CategoryUiState()
    data class Value(val dishes: List<CategoryItem>) : CategoryUiState()
}