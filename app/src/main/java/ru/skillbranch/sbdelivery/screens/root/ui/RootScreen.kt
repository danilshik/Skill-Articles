package ru.skillbranch.sbdelivery.screens.root.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.screens.cart.ui.CartScreen
import ru.skillbranch.sbdelivery.screens.components.AboutDialog
import ru.skillbranch.sbdelivery.screens.components.DefaultToolbar
import ru.skillbranch.sbdelivery.screens.components.DishesToolbar
import ru.skillbranch.sbdelivery.screens.components.NavigationDrawer
import ru.skillbranch.sbdelivery.screens.dish.ui.DishScreen
import ru.skillbranch.sbdelivery.screens.dishes.ui.DishesScreen
import ru.skillbranch.sbdelivery.screens.favorites.ui.FavoriteScreen
import ru.skillbranch.sbdelivery.screens.home.ui.HomeScreen
import ru.skillbranch.sbdelivery.screens.menu.ui.MenuScreen
import ru.skillbranch.sbdelivery.screens.root.RootViewModel
import ru.skillbranch.sbdelivery.screens.root.logic.*

@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun RootScreen(vm: RootViewModel) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            vm.notifications
                .collect { notification -> renderNotification(notification, scaffoldState, vm) }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppbarHost(vm, onToggleDrawer = {
                val drawerState = scaffoldState.drawerState
                if (drawerState.isOpen) scope.launch { drawerState.close() }
                else scope.launch { drawerState.open() }
            })
        },
        content = { ContentHost(vm) },
        drawerContent = {
            val state = vm.state.collectAsState().value
            NavigationDrawer(
                currentRoute = state.currentRoute,
                cartCount = state.cartCount,
                notificationCount = state.notificationCount,
                onLogout = { //TODO: empty handler
                            }
//                user = state.user,
            ) { route ->
                if(state.currentRoute == route) return@NavigationDrawer
                vm.navigate(NavCmd.To(route))
                scope.launch { scaffoldState.drawerState.close() }
            }
        },
        drawerScrimColor = MaterialTheme.colors.primaryVariant.copy(alpha = DrawerDefaults.ScrimOpacity),
        snackbarHost = { host ->
            SnackbarHost(
                hostState = host,
                snackbar = {
                    Snackbar(
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        action = {
                            TextButton(
                                onClick = { host.currentSnackbarData?.performAction() }
                            ) {
                                Text(
                                    text = host.currentSnackbarData?.actionLabel?.toUpperCase()
                                        ?: "",
                                    color = MaterialTheme.colors.secondary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        },
                        content = {
                            Text(
                                text = host.currentSnackbarData?.message ?: "",
                                color = MaterialTheme.colors.onBackground
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                })
        },
    )
}

@ExperimentalCoroutinesApi
private suspend fun renderNotification(
    notification: Eff.Notification,
    scaffoldState: ScaffoldState,
    vm: RootViewModel
) {
    val result = when (notification) {
        is Eff.Notification.Error -> {

            val (message, label) = notification
            scaffoldState.snackbarHostState.showSnackbar(message, label)
        }
        is Eff.Notification.Action -> {
            val (message, label) = notification
            scaffoldState.snackbarHostState.showSnackbar(message, label)
        }
        is Eff.Notification.Text -> scaffoldState.snackbarHostState.showSnackbar(
            notification.message
        )
    }

    when (result) {
        SnackbarResult.ActionPerformed -> {
            when (notification) {
                is Eff.Notification.Error -> notification.action?.let(vm::accept)
                is Eff.Notification.Action -> vm.accept(notification.action)
                else -> { /*  no action needed */
                }
            }
        }
        SnackbarResult.Dismissed -> {
            /* dismissed, no action needed */
        }
    }
}

@Composable
fun Navigation(
    currentScreen: ScreenState,
    modifier: Modifier = Modifier,
    content: @Composable (ScreenState) -> Unit
) {
    val restorableStateHolder = rememberSaveableStateHolder()

    Box(modifier) {
        restorableStateHolder.SaveableStateProvider(currentScreen.route + currentScreen.title) {
            content(currentScreen)
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ContentHost(vm: RootViewModel) {
    val state: RootState by vm.state.collectAsState()
    val screen: ScreenState = state.current
    Navigation(screen, Modifier.fillMaxSize()) { currentScreen ->
        when (currentScreen) {
            is ScreenState.Dishes -> DishesScreen(
                currentScreen.state,
                vm::accept
            )
            is ScreenState.Dish -> DishScreen(
                currentScreen.state,
                vm::accept
            )
            is ScreenState.Favorites -> FavoriteScreen(
                currentScreen.state,
                vm::accept
            )
            is ScreenState.Cart -> CartScreen(
                currentScreen.state,
                vm::accept
            )
            is ScreenState.Home -> HomeScreen(
                currentScreen.state,
                vm::accept
            )

            is ScreenState.Menu -> MenuScreen(
                currentScreen.state,
                vm::accept
            )

            is ScreenState.About -> AboutDialog() {
                vm.navigate(NavCmd.Back)
            }
        }
    }
}


@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun AppbarHost(vm: RootViewModel, onToggleDrawer: () -> Unit) {
    val state: RootState by vm.state.collectAsState()
    when (val screen: ScreenState = state.current) {
        is ScreenState.Dishes -> DishesToolbar(
            title = screen.title,
            state = screen.state,
            cartCount = state.cartCount,
            canBack = true,
            accept = { vm.accept(Msg.Dishes(it)) },
            onCart = { vm.navigate(NavCmd.ToCart) },
            onDrawer = onToggleDrawer
        )

        is ScreenState.Favorites -> DishesToolbar(
            title = screen.title,
            state = screen.state,
            canBack = false,
            cartCount = state.cartCount,
            accept = { vm.accept(Msg.Dishes(it)) },
            onCart = { vm.navigate(NavCmd.ToCart) },
            onDrawer = onToggleDrawer
        )

        is ScreenState.Menu -> DefaultToolbar(
            screen.title,
            state.cartCount,
            canBack = screen.state.parent != null,
            onCart = { vm.navigate(NavCmd.ToCart) },
            onDrawer = onToggleDrawer
        )

        is ScreenState.Dish -> DefaultToolbar(
            screen.title,
            state.cartCount,
            canBack = true,
            onCart = { vm.navigate(NavCmd.ToCart) },
            onDrawer = onToggleDrawer
        )

        else -> DefaultToolbar(
            screen.title,
            state.cartCount,
            onCart = { vm.navigate(NavCmd.ToCart) },
            onDrawer = onToggleDrawer
        )
    }
}