package ru.skillbranch.sbdelivery.screens.root.logic

import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesState
import ru.skillbranch.sbdelivery.screens.favorites.logic.FavoriteFeature
import ru.skillbranch.sbdelivery.screens.home.logic.HomeFeature
import ru.skillbranch.sbdelivery.screens.menu.logic.MenuFeature
import java.lang.IllegalStateException

fun RootState.reduceNavigate(
    msg: NavCmd
): Pair<RootState, Set<Eff>> {
    val navEff = Eff.Terminate(currentRoute)

    return when (msg) {
        is NavCmd.Back -> {
            val newBackstack = backstack.dropLast(1)
            val newScreen: ScreenState? = backstack.lastOrNull()
            if (newScreen == null) this to setOf(Eff.Cmd(Command.Finish))
            else {
                val newList = screens.toMutableMap()
                    .also { screens -> screens[newScreen.route] = newScreen }

                copy(
                    screens = newList,
                    backstack = newBackstack,
                    currentRoute = newScreen.route
                ) to newScreen.initialEffects()
            }
        }

        NavCmd.ToCart -> {
            val newState = screenStateFactory<ScreenState.Cart>(CartFeature.route) {
                copy(state = CartFeature.initialState())
            }
            newState to newState.current.initialEffects()
        }

        is NavCmd.ToCategory -> {
            val newState = screenStateFactory<ScreenState.Dishes>(DishesFeature.route) {
                copy(state = DishesFeature.initialState(title = msg.title, category = msg.id))
            }
            val newEff = DishesFeature.initialEffects(msg.id)
                .mapTo(HashSet(), Eff::Dishes)

            newState to newEff
        }

        is NavCmd.ToDishItem -> {
            val newState = screenStateFactory<ScreenState.Dish>(DishFeature.route) {
                copy(state = DishFeature.initialState(id = msg.id, title = msg.title))
            }
            newState to newState.current.initialEffects()
        }

        is NavCmd.To -> {
            val newBackstack = backstack.plus(current)
            val newState = copy(currentRoute = msg.route, backstack = newBackstack)

            when (msg.route) {
                HomeFeature.route -> {
                    newState.changeCurrentScreen<ScreenState.Home> {
                        copy(state = HomeFeature.initialState())
                    }
                }
                MenuFeature.route -> {
                    newState.changeCurrentScreen<ScreenState.Menu> {
                        copy(state = MenuFeature.initialState())
                    }
                }

                FavoriteFeature.route -> {
                    newState.changeCurrentScreen<ScreenState.Favorites> {
                        copy(state = FavoriteFeature.initialState())
                    }
                }

                "about" -> {
                    newState.changeCurrentScreen<ScreenState.About> {
                        copy(state=Unit)
                    }

                }


                else -> throw IllegalStateException("not found navigation for route ${msg.route}")
            }

            newState to newState.current.initialEffects()
        }


    }.run { first to second.plus(navEff)}


}

fun <T : ScreenState> RootState.screenStateFactory(route: String, block: T.() -> T): RootState {
    val newBackstack = backstack.plus(current)
    val newState = copy(currentRoute = route, backstack = newBackstack)
    return newState.changeCurrentScreen(block)
}