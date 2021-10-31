package ru.skillbranch.sbdelivery.screens.favorites.logic
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesState


object FavoriteFeature {
    const val route: String = "favorites"

    fun initialState(): DishesState = DishesState()
    fun initialEffects(): Set<Eff> = setOf(Eff.FindDishes)

    sealed class Eff {
        object  FindDishes : Eff()
        data class SearchDishes( val query: String) : Eff()
        data class FindSuggestions(val query: String) : Eff()
    }
}
