package ru.skillbranch.sbdelivery.screens.home.logic
import ru.skillbranch.sbdelivery.domain.DishItem
import ru.skillbranch.sbdelivery.screens.dishes.data.DishesUiState
import java.io.Serializable


object HomeFeature {
    const val route: String = "home"

    fun initialState(): State = State()
    fun initialEffects(): Set<Eff> = setOf(Eff.SyncRecommended, Eff.FindBest, Eff.FindPopular)

    data class State(
        val recommended: DishesUiState = DishesUiState.Loading,
        val best: DishesUiState = DishesUiState.Loading,
        val popular: DishesUiState = DishesUiState.Loading
    ): Serializable

    sealed class Eff {
        object SyncRecommended : Eff()
        object FindBest : Eff()
        object FindPopular : Eff()
    }

    sealed class Msg {
        data class ShowRecommended(val dishes: List<DishItem>) : Msg()
        data class ShowBest(val dishes: List<DishItem>):Msg()
        data class ShowPopular(val dishes: List<DishItem>):Msg()
    }
}

