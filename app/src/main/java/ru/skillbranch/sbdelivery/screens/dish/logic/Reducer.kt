package ru.skillbranch.sbdelivery.screens.dish.logic

import ru.skillbranch.sbdelivery.screens.dish.data.DishUiState
import ru.skillbranch.sbdelivery.screens.dish.data.ReviewUiState
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

fun Set<DishFeature.Eff>.toEffs(): Set<Eff> = mapTo(HashSet(), Eff::Dish)

fun DishFeature.State.reduce(
    msg: DishFeature.Msg,
    rootState: RootState
): Pair<RootState, Set<Eff>> {
    val (screenState, effs) = selfReduce(msg)
    return rootState.changeCurrentScreen<ScreenState.Dish> { copy(state = screenState) } to effs
}

fun DishFeature.State.selfReduce(msg: DishFeature.Msg) =
    when (msg) {
        is DishFeature.Msg.DecrementCount -> {
            if (count <= 1) this to emptySet()
            else copy(count = count.dec()) to emptySet()
        }

        is DishFeature.Msg.SendReview -> {
            val currentReviews = if(reviews is ReviewUiState.Value) reviews.list else emptyList()
            copy(
                isReviewDialog = false,
                reviews = ReviewUiState.ValueWithLoading(currentReviews)
            ) to setOf(DishFeature.Eff.SendReview(msg.dishId, msg.rating, msg.review)).toEffs()
        }

        is DishFeature.Msg.AddToCart -> copy(count = 1) to setOf(
            DishFeature.Eff.AddToCart(msg.id, msg.count)
        ).toEffs()

        is DishFeature.Msg.ShowReviews -> {
            if (msg.reviews.isNotEmpty()) copy(reviews = ReviewUiState.Value(msg.reviews)) to emptySet()
            else copy(reviews = ReviewUiState.Empty) to emptySet()
        }

        is DishFeature.Msg.ShowDish -> copy(content = DishUiState.Value(msg.dish)) to emptySet()
        is DishFeature.Msg.IncrementCount -> copy(count = count.inc()) to emptySet()
        is DishFeature.Msg.ShowReviewDialog -> copy(isReviewDialog = true) to emptySet()
        is DishFeature.Msg.HideReviewDialog -> copy(isReviewDialog = false) to emptySet()

    }