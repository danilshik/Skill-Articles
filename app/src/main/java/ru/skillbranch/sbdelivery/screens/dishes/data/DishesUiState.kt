package ru.skillbranch.sbdelivery.screens.dishes.data

import ru.skillbranch.sbdelivery.domain.DishItem
import java.io.Serializable

sealed class DishesUiState : Serializable {
    object Loading : DishesUiState()
    object Empty : DishesUiState()
    object Error : DishesUiState()
    data class Value(val dishes: List<DishItem>) : DishesUiState()
}