package ru.skillbranch.sbdelivery.screens.dishes.logic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.repository.DishesRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class DishesEffHandler @Inject constructor(
    val repository: DishesRepository,
    private val notifyChanel: Channel<Eff.Notification>,
    override var localJob: Job
) : IEffectHandler<DishesFeature.Eff, Msg> {

    private val errHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
        t.message?.let { notifyChanel.trySend(Eff.Notification.Error(it)) }
    }

    override suspend fun handle(
        eff: DishesFeature.Eff,
        commit: (Msg) -> Unit
    ) {
        CoroutineScope(coroutineContext + localJob + errHandler).launch {
            when (eff) {
                is DishesFeature.Eff.FindDishes -> {
                    commit(DishesMsg.ShowLoading.toMsg())

                    repository.findDishesByCategory(eff.category)
                        .map(DishesMsg::ShowDishes)
                        .map(Msg::Dishes)
                        .collect { commit(it) }
                }

                is DishesFeature.Eff.SearchDishes -> {
                    commit(DishesMsg.ShowLoading.toMsg())

                    repository.searchDishes(eff.category, eff.query)
                        .map(DishesMsg::ShowDishes)
                        .map(Msg::Dishes)
                        .collect { commit(it) }
                }

                is DishesFeature.Eff.FindSuggestions -> {
                    repository.findSuggestions(eff.category, eff.query)
                        .map(DishesMsg::ShowSuggestion)
                        .map(Msg::Dishes)
                        .collect { commit(it) }
                }
            }
        }
    }


}

fun DishesMsg.toMsg(): Msg = Msg.Dishes(this)