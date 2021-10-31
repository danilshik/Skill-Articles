package ru.skillbranch.sbdelivery.screens.home.logic

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ru.skillbranch.sbdelivery.repository.DishesRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@FlowPreview
class HomeEffHandler @Inject constructor(
    val repository: DishesRepository,
    private val notifyChanel: Channel<Eff.Notification>,
    override var localJob: Job
) : IEffectHandler<HomeFeature.Eff, Msg> {

    private val errHandler = CoroutineExceptionHandler{_, t ->
        t.printStackTrace()
        t.message?.let { notifyChanel.trySend(Eff.Notification.Error(it)) }
    }

    override suspend fun handle(
        eff: HomeFeature.Eff,
        commit: (Msg) -> Unit
    ) {
        CoroutineScope(coroutineContext + localJob + errHandler).launch {
            when (eff) {
                HomeFeature.Eff.FindBest -> {
                    repository.findBest()
                        .map(HomeFeature.Msg::ShowBest)
                        .map(Msg::Home)
                        .collect { commit(it) }

                }

                HomeFeature.Eff.FindPopular -> {
                    repository.findPopular()
                        .map(HomeFeature.Msg::ShowPopular)
                        .map(Msg::Home)
                        .collect { commit(it) }
                }

                HomeFeature.Eff.SyncRecommended -> {

                    val ids = repository.getRecommended()
                    if (ids.isEmpty()) {
                        HomeFeature.Msg.ShowRecommended(emptyList())
                            .let(Msg::Home)
                            .also(commit)
                    } else {
                        val dishes = repository.findRecommended(ids)

                        //async sync recommended if need
                        launch {
                            dishes
                                .take(1)
                                .map { items -> items.map { it.id } }
                                .onEach { Log.e("HomeEffHandler", "exist $it") }
                                .map { existIds -> ids.filter { !existIds.contains(it) } }
                                .onEach { Log.e("HomeEffHandler", "needReload $it") }
                                .collect{ repository.syncRecommended(it).asFlow() }
                        }

                        //show recommended
                        launch {
                            dishes
                                .distinctUntilChanged()
                                .map(HomeFeature.Msg::ShowRecommended)
                                .map(Msg::Home)
                                .onEach { Log.e("HomeEffHandler", "ShowRecomend $it") }
                                .collect { commit(it) }
                        }

                    }
                }
            }
        }
    }
}