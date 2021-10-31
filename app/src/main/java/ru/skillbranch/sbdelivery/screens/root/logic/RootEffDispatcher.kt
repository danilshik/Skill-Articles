package ru.skillbranch.sbdelivery.screens.root.logic

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import ru.skillbranch.sbdelivery.repository.RootRepository
import ru.skillbranch.sbdelivery.screens.cart.logic.CartEffHandler
import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.dish.logic.DishEffHandler
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesEffHandler
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.favorites.logic.FavoriteEffHandler
import ru.skillbranch.sbdelivery.screens.favorites.logic.FavoriteFeature
import ru.skillbranch.sbdelivery.screens.home.logic.HomeEffHandler
import ru.skillbranch.sbdelivery.screens.home.logic.HomeFeature
import ru.skillbranch.sbdelivery.screens.menu.logic.MenuEffHandler
import ru.skillbranch.sbdelivery.screens.menu.logic.MenuFeature
import javax.inject.Inject

@FlowPreview
class EffectDispatcher @Inject constructor(
    private val dishesHandler: DishesEffHandler,
    private val dishHandler: DishEffHandler,
    private val cartHandler: CartEffHandler,
    private val homeHandler: HomeEffHandler,
    private val menuHandler: MenuEffHandler,
    private val favoriteHandler: FavoriteEffHandler,
    private val repository: RootRepository,

    private val _cmdChanel: Channel<Command>,
    private val _notifyChanel: Channel<Eff.Notification>,

    ) : IEffectHandler<Eff, Msg> {
    //for android command
    val commands = _cmdChanel.receiveAsFlow()

    //for notification / UI effects
    val notifications = _notifyChanel.receiveAsFlow()

    override suspend fun handle(eff: Eff, commit: (Msg) -> Unit) {
        Log.w("EFFECT", "$eff")
        when (eff) {
            //feature effects
            is Eff.Dishes -> dishesHandler.handle(eff.eff, commit)
            is Eff.Dish -> dishHandler.handle(eff.eff, commit)
            is Eff.Cart -> cartHandler.handle(eff.eff, commit)
            is Eff.Home -> homeHandler.handle(eff.eff, commit)
            is Eff.Menu -> menuHandler.handle(eff.eff, commit)
            is Eff.Favorite -> favoriteHandler.handle(eff.eff, commit)

            //sync effects
            is Eff.SyncCounter -> {
                repository.cartCount()
                    .map(Msg::UpdateCartCount)
                    .collect { commit(it) }

            }

            is Eff.SyncEntity -> {
                coroutineScope {
                    launch {
                        val isEmpty = repository.isEmptyDishes()
                        if (isEmpty) repository.syncDishes()
                    }
                    launch {
                        val isEmpty = repository.isEmptyCategories()
                        if (isEmpty) repository.syncCategories()
                    }
                }

            }

            //global effects
            is Eff.AddToCart -> {
                repository.addDishToCart(eff.id)
                _notifyChanel.send(
                    Eff.Notification.Action(
                        "${eff.title} успешно добавлен в корзину",
                        label = "Отмена",
                        action = Msg.RemoveFromCart(eff.id, eff.title)
                    )
                )
            }

            is Eff.RemoveFromCart -> {
                repository.removeDishFromCart(eff.id)
                _notifyChanel.send(Eff.Notification.Text("${eff.title} удален из корзины"))
            }

            is Eff.ToggleLike -> {
                if (eff.isFavorite) repository.insertFavorite(eff.id)
                else repository.removeFavorite(eff.id)
            }

            //core effects
            is Eff.Nav -> commit(Msg.Navigate(eff.cmd))
            is Eff.Cmd -> _cmdChanel.send(eff.command)
            is Eff.Notification -> _notifyChanel.send(eff)
            is Eff.Terminate -> when (eff.route) {
                HomeFeature.route -> homeHandler.cancelJob()
                DishesFeature.route -> dishesHandler.cancelJob()
                DishFeature.route -> dishHandler.cancelJob()
                CartFeature.route -> cartHandler.cancelJob()
                MenuFeature.route -> menuHandler.cancelJob()
                FavoriteFeature.route -> favoriteHandler.cancelJob()
            }

        }
    }

    override var localJob: Job = SupervisorJob()
}