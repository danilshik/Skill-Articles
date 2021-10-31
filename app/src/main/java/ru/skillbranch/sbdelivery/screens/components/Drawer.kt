package ru.skillbranch.sbdelivery.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.domain.User
import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.favorites.logic.FavoriteFeature
import ru.skillbranch.sbdelivery.screens.home.logic.HomeFeature
import ru.skillbranch.sbdelivery.screens.menu.logic.MenuFeature
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

data class MenuItem(@DrawableRes val icon: Int, val title: String, val route: String)

val menuItems = listOf(
    MenuItem(R.drawable.ic_home, "Главная", HomeFeature.route),
    MenuItem(R.drawable.ic_menu, "Меню", MenuFeature.route),
    MenuItem(R.drawable.ic_favorite, "Избраное", FavoriteFeature.route),
    MenuItem(R.drawable.ic_baseline_shopping_cart_24, "Корзина", CartFeature.route),
    MenuItem(R.drawable.ic_user, "Профиль", "profile"),
    MenuItem(R.drawable.ic_orders, "Заказы", "order"),
    MenuItem(R.drawable.ic_notification, "Уведомления", "notifications"),
)

@Composable
fun NavigationDrawer(
    currentRoute: String,
    modifier: Modifier = Modifier,
    user: User? = User("Сидоров Иван", "sidorov.ivan@mail.ru"),
    notificationCount: Int = 0,
    cartCount: Int = 0,
    onLogout: () -> Unit,
    onSelect: (String) -> Unit
) {
    Column(modifier = modifier.background(color = MaterialTheme.colors.surface)) {
        user?.let {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.7f)
                    .background(color = MaterialTheme.colors.background)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_drawer),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = it.name, style = MaterialTheme.typography.subtitle1)
                    Text(text = it.email, style = MaterialTheme.typography.body1)
                }

                IconButton(onClick = onLogout,
                    modifier= Modifier.offset(x = (-12).dp, y = (-12).dp)
                ) {
                    Icon(
                        tint = MaterialTheme.colors.onBackground,
                        painter = painterResource(id = R.drawable.ic_baseline_exit_to_app_24),
                        contentDescription = "Logout"
//                        contentDescription = "exit"
                    )
                }
            }
        }

        menuItems.forEach { menuItem ->

            val isSelected = currentRoute == menuItem.route
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth()
                    .clickable { onSelect(menuItem.route) }
                    .then(if (isSelected) Modifier.background(MaterialTheme.colors.secondary) else Modifier)
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    tint = if (isSelected) MaterialTheme.colors.onSecondary else MaterialTheme.colors.secondary,
                    painter = painterResource(id = menuItem.icon),
                    contentDescription = menuItem.title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = menuItem.title,
                    style = MaterialTheme.typography.subtitle2
                )

                if (menuItem.route == CartFeature.route && cartCount > 0) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "+$cartCount",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                if (menuItem.route == "notifications" && notificationCount > 0) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "+$notificationCount",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .clickable { onSelect("about") }
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                tint = MaterialTheme.colors.secondary,
                painter = painterResource(id = R.drawable.ic_about),
                contentDescription = "О приложении",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "О приложении",
                style = MaterialTheme.typography.subtitle2
            )
        }

    }
}

@Preview
@Composable
fun DrawerPreview() {
    AppTheme {
        NavigationDrawer("home", notificationCount = 7, cartCount = 8, onLogout = {} ) {

        }
    }

}