package ru.skillbranch.sbdelivery.screens.menu.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.domain.CategoryItem
import ru.skillbranch.sbdelivery.screens.components.LazyGrid
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@Composable
fun MenuItem(item: CategoryItem, modifier: Modifier = Modifier, onClick: (CategoryItem) -> Unit) {
    Card(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clickable { onClick(item) }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val painter = rememberImagePainter(
                data = item.icon, //this is svg icon
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.img_empty_place_holder)
                })

            Icon(
                painter = painter,
                contentDescription = item.title,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
@Preview
fun MenuPreview() {
    AppTheme {
        Row() {
            MenuItem(
                item = CategoryItem("0", "test test ", "null", 0, null),
                modifier = Modifier.requiredWidth(140.dp),
                onClick = {})

            MenuItem(
                item = CategoryItem("0", "test test test test test test", "null", 0, null),
                modifier = Modifier.requiredWidth(140.dp),
                onClick = {})
        }

    }
}

@Composable
@Preview
fun MenuGridPreview() {

    val items = listOf(
        CategoryItem("0", "test", null, 0, null),
        CategoryItem("1", "test1", null, 1, null),
        CategoryItem("2", "test2", null, 2, null),
        CategoryItem("3", "test2", null, 2, null),
//        CategoryItem("4", "test2", null, 2, null),
    )
    AppTheme {
        LazyGrid(
            items = items,
            cols = 3,
            cellsPadding = 16.dp
        ) {
            MenuItem(it) { }
        }
    }
}

