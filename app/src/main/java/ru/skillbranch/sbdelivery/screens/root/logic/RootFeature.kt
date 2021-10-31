package ru.skillbranch.sbdelivery.screens.root.logic

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.domain.User
import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.cart.logic.reduce
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.dish.logic.reduce
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesMsg
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesState
import ru.skillbranch.sbdelivery.screens.dishes.logic.reduceCategory
import ru.skillbranch.sbdelivery.screens.favorites.logic.FavoriteFeature
import ru.skillbranch.sbdelivery.screens.favorites.logic.reduceFavorite
import ru.skillbranch.sbdelivery.screens.home.logic.HomeFeature
import ru.skillbranch.sbdelivery.screens.home.logic.reduce
import ru.skillbranch.sbdelivery.screens.menu.logic.MenuFeature
import ru.skillbranch.sbdelivery.screens.menu.logic.reduce
import java.io.Serializable

@FlowPreview
@ExperimentalCoroutinesApi
class RootFeature(private val initState: RootState? = null) {

    private fun initialState(): RootState = initState ?: RootState(
        screens = mapOf(
            HomeFeature.route to ScreenState.Home(),
            DishesFeature.route to ScreenState.Dishes(),
            FavoriteFeature.route to ScreenState.Favorites(),
            DishFeature.route to ScreenState.Dish(),
            CartFeature.route to ScreenState.Cart(),
            MenuFeature.route to ScreenState.Menu(),
            "about" to ScreenState.About(Unit)
        ),
        currentRoute = HomeFeature.route
    )

    private fun initialEffects(): Set<Eff> =
        setOf(Eff.SyncEntity, Eff.SyncCounter) + HomeFeature.initialEffects()
            .mapTo(HashSet(), Eff::Home)

    private lateinit var _scope: CoroutineScope

    private val _state: MutableStateFlow<RootState> = MutableStateFlow(initialState())
    val state
        get() = _state.asStateFlow()


    private val mutations: MutableSharedFlow<Msg> = MutableSharedFlow()

    fun listen(scope: CoroutineScope, handleDispatcher: EffectDispatcher, inState: RootState?) {
        _scope = scope
        _scope.launch {
            mutations
                .scan(
                    (inState ?: initialState()) to initialEffects()
                ) { (s, _), m -> reduceDispatcher(s, m) }
                .collect { (s, es) ->
                    _state.emit(s)
                    es.forEach {
                        launch {
                            handleDispatcher.handle(it, ::mutate)
                        }
                    }
                }

        }
    }

    fun mutate(mutation: Msg) {
        Log.w("MUTATION", "$mutation")
        _scope.launch {
            mutations.emit(mutation)
        }
    }

    private fun reduceDispatcher(root: RootState, msg: Msg): Pair<RootState, Set<Eff>> {
        return when {
            msg is Msg.Navigate -> root.reduceNavigate(msg.msg)

            msg is Msg.UpdateCartCount -> root.copy(cartCount = msg.count) to emptySet()
            msg is Msg.ToggleLike -> root to setOf(Eff.ToggleLike(msg.id, msg.isFavorite))
            msg is Msg.AddToCart -> root to setOf(Eff.AddToCart(msg.id, msg.title))
            msg is Msg.RemoveFromCart -> root to setOf(Eff.RemoveFromCart(msg.id, msg.title))
            msg is Msg.ClickDish -> root to setOf(Eff.Nav(NavCmd.ToDishItem(msg.id, msg.title)))

            msg is Msg.Dishes && root.current is ScreenState.Dishes -> root.current.state.reduceCategory(
                msg.msg,
                root
            )
            msg is Msg.Dishes && root.current is ScreenState.Favorites -> root.current.state.reduceFavorite(
                msg.msg,
                root
            )
            msg is Msg.Dish && root.current is ScreenState.Dish -> root.current.state.reduce(
                msg.msg,
                root
            )
            msg is Msg.Cart && root.current is ScreenState.Cart -> root.current.state.reduce(
                msg.msg,
                root
            )
            msg is Msg.Home && root.current is ScreenState.Home -> root.current.state.reduce(
                msg.msg,
                root
            )
            msg is Msg.Menu && root.current is ScreenState.Menu -> root.current.state.reduce(
                msg.msg,
                root
            )

            else -> root to emptySet()
        }
    }
}


data class RootState(
    val screens: Map<String, ScreenState>,
    val currentRoute: String,
    val backstack: List<ScreenState> = emptyList(),
    val cartCount: Int = 0,
    val notificationCount: Int = 0,
    val user: User? = null
) : Serializable {
    val current: ScreenState =
        checkNotNull(screens[currentRoute], { "check route $currentRoute or scrrens $screens" })

    fun <T : ScreenState> changeCurrentScreen(block: T.() -> T): RootState {
        @Suppress("UNCHECKED_CAST") val newScreen = (current as? T)?.block()
        val newList = if (newScreen != null)
            screens.toMutableMap().also { mutableScreens ->
                mutableScreens[currentRoute] = newScreen
            } else screens
        return copy(screens = newList)
    }
}

sealed class ScreenState(
    public val route: String,
    val title: String
) : Serializable {
    abstract fun initialEffects(): Set<Eff>

    data class Dish(
        val state: DishFeature.State =DishFeature.State()
    ) : ScreenState(DishFeature.route, state.title) {
        override fun initialEffects(): Set<Eff> = DishFeature
            .initialEffects(state.id)
            .mapTo(HashSet(), Eff::Dish)

    }

    data class Dishes(
        val state: DishesState = DishesState()
    ) : ScreenState(DishesFeature.route, state.title) {
        override fun initialEffects(): Set<Eff> = DishesFeature
            .initialEffects(state.category)
            .mapTo(HashSet(), Eff::Dishes)
    }

    data class Cart(
        val state: CartFeature.State = CartFeature.State()
    ) : ScreenState(CartFeature.route, "Корзина") {
        override fun initialEffects(): Set<Eff> = CartFeature
            .initialEffects()
            .mapTo(HashSet(), Eff::Cart)
    }

    data class Home(
        val state: HomeFeature.State = HomeFeature.State()
    ) : ScreenState(HomeFeature.route, "Главная") {
        override fun initialEffects(): Set<Eff> = HomeFeature
            .initialEffects()
            .mapTo(HashSet(), Eff::Home)
    }

    data class Menu(
        val state: MenuFeature.State = MenuFeature.State()
    ) : ScreenState(MenuFeature.route, "Меню") {
        override fun initialEffects(): Set<Eff> = MenuFeature
            .initialEffects()
            .mapTo(HashSet(), Eff::Menu)
    }

    data class Favorites(
        val state: DishesState = DishesState()
    ) : ScreenState(FavoriteFeature.route, "Избраное") {
        override fun initialEffects(): Set<Eff> = FavoriteFeature
            .initialEffects()
            .mapTo(HashSet(), Eff::Favorite)
    }

    data class About(
        val state: Unit
    ): ScreenState("about", "О программе")
    {
        override fun initialEffects(): Set<Eff> = setOf(Eff.About(Unit))
    }
}

sealed class Eff {
    data class Cmd(val command: Command) : Eff()
    sealed class Notification(open val message: String) : Eff() {

        data class Text(override val message: String) : Notification(message)
        data class Error(
            override val message: String,
            val label: String? = null,
            val action: Msg? = null
        ) : Notification(message)

        data class Action(
            override val message: String,
            val label: String,
            val action: Msg
        ) : Notification(message)
    }

    data class Dish(val eff: DishFeature.Eff) : Eff()
    data class Dishes(val eff: DishesFeature.Eff) : Eff()
    data class Cart(val eff: CartFeature.Eff) : Eff()
    data class Home(val eff: HomeFeature.Eff) : Eff()
    data class Menu(val eff: MenuFeature.Eff) : Eff()
    data class Favorite(val eff: FavoriteFeature.Eff) : Eff()
    data class About(val eff: Unit) : Eff()

    //Navigate
    data class Nav(val cmd: NavCmd) : Eff()

    //Sync
    object SyncCounter : Eff()
    object SyncEntity : Eff()

    //Global
    data class ToggleLike(val id: String, val isFavorite: Boolean) : Eff()
    data class AddToCart(val id: String, val title: String) : Eff()
    data class RemoveFromCart(val id: String, val title: String) : Eff()

    //Terminate running coroutines
    data class Terminate(val route: String) : Eff()
}


sealed class Msg {
    data class Dish(val msg: DishFeature.Msg) : Msg()
    data class Dishes(val msg: DishesMsg) : Msg()
    data class Cart(val msg: CartFeature.Msg) : Msg()

    data class Navigate(val msg: NavCmd) : Msg()
    data class UpdateCartCount(val count: Int) : Msg()
    data class Home(val msg: HomeFeature.Msg) : Msg()
    data class Menu(val msg: MenuFeature.Msg) : Msg()

    data class ToggleLike(val id: String, val isFavorite: Boolean) : Msg()
    data class AddToCart(val id: String, val title: String) : Msg()
    data class RemoveFromCart(val id: String, val title: String) : Msg()
    data class ClickDish(val id: String, val title: String) : Msg()
}

sealed class NavCmd {
    data class To(val route: String) : NavCmd()
    object ToCart : NavCmd()
    data class ToDishItem(val id: String, val title: String) : NavCmd()
    data class ToCategory(val id: String, val title: String) : NavCmd()

    object Back : NavCmd()
}

sealed class Command {
    object Finish : Command()
}





