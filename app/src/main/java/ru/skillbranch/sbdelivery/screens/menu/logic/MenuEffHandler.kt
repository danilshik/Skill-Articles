package ru.skillbranch.sbdelivery.screens.menu.logic

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.repository.CategoriesRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MenuEffHandler @Inject constructor(
    val repository: CategoriesRepository,
    private val notifyChanel: Channel<Eff.Notification>,
    override var localJob: Job
) : IEffectHandler<MenuFeature.Eff, Msg> {

    override suspend fun handle(
        eff: MenuFeature.Eff,
        commit: (Msg) -> Unit
    ) {
        CoroutineScope(coroutineContext + localJob).launch {
            when (eff) {
                MenuFeature.Eff.FindCategories -> {
                    repository.findCategories()
                        .map(MenuFeature.Msg::ShowMenu)
                        .map(Msg::Menu)
                        .collect { commit(it) }
                }
            }
        }
    }
}