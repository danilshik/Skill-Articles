package ru.skillbranch.sbdelivery.screens.dishes.logic

import ru.skillbranch.sbdelivery.domain.DishItem
import ru.skillbranch.sbdelivery.screens.dishes.data.DishesUiState
import java.io.Serializable


object DishesFeature {
    const val route: String = "dishes"

    fun initialState(title: String, category: String): DishesState =
        DishesState(title = title, category = category)

    fun initialEffects(category: String): Set<Eff> = setOf<Eff>(Eff.FindDishes(category))

    sealed class Eff {
        data class FindDishes(val category: String) : Eff()
        data class SearchDishes(val category: String, val query: String) : Eff()
        data class FindSuggestions(val category: String, val query: String) : Eff()
    }

}


data class DishesState(
    val category: String = "",
    val title: String = "",
    val input: String = "",
    val isSearch: Boolean = false,
    val suggestions: Map<String, Int> = emptyMap(),
    val list: DishesUiState = DishesUiState.Loading,

    ) : Serializable

sealed class DishesMsg {
    data class SearchInput(val newInput: String) : DishesMsg()
    data class ShowDishes(val dishes: List<DishItem>) : DishesMsg()
    data class SearchSubmit(val query: String) : DishesMsg()
    data class UpdateSuggestionResult(val query: String) : DishesMsg()
    data class ShowSuggestion(val sug: Map<String, Int>) : DishesMsg()
    data class SuggestionSelect(val it: String) : DishesMsg()

    object SearchToggle : DishesMsg()
    object ConnectionFailed : DishesMsg()
    object ShowLoading : DishesMsg()
}