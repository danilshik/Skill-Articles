package ru.skillbranch.sbdelivery.screens.dish.data

import ru.skillbranch.sbdelivery.domain.Dish
import java.io.Serializable

sealed class DishUiState : Serializable {
    object Loading : DishUiState()
    data class Value(val data: Dish) : DishUiState()
}
