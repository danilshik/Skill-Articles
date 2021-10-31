package ru.skillbranch.sbdelivery.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.domain.DishItem
import ru.skillbranch.sbdelivery.screens.components.items.ProductItem
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@Composable
fun <T> Grid(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    val chunkedList = items.chunked(cols)

    Column(
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        modifier = modifier.padding(contentPadding)
    ) {
        chunkedList.forEach { chunk ->
            Row {
                repeat(chunk.size) {
                    Box(modifier = Modifier.weight(1f / cols)) {
                        itemContent(chunk[it])
                    }

                    if (it < chunk.size.dec()) Spacer(modifier = Modifier.width(cellsPadding))
                }

                if (chunk.size % cols > 0) {
                    val emptyCols = cols - chunk.size % cols
                    repeat(emptyCols) {
                        Spacer(modifier = Modifier.width(cellsPadding))
                    }
                    Spacer(modifier = Modifier.weight(emptyCols / cols.toFloat()))
                }
            }

        }
    }
}

@Composable
fun <T> LazyGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
){
    val chunkedList = items.chunked(cols)

    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        modifier = modifier
    ) {
        items(chunkedList){ chunk ->
            Row {
                repeat(chunk.size) {
                    Box(modifier = Modifier.weight(1f / cols)) {
                        itemContent(chunk[it])
                    }

                    if (it < chunk.size.dec()) Spacer(modifier = Modifier.width(cellsPadding))
                }

                if (chunk.size % cols > 0) {
                    val emptyCols = cols - chunk.size % cols
                    repeat(emptyCols) {
                        Spacer(modifier = Modifier.width(cellsPadding))
                    }
                    Spacer(modifier = Modifier.weight(emptyCols / cols.toFloat()))
                }
            }

        }
    }
}

@Preview
@Composable
fun GridPreview() {
    val dishes = listOf(
        DishItem(
            id = "5ed8da011f071c00465b2026",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
            price = "170",
            title = "Бургер \"Америка\"",
            isFavorite = true
        ),
        DishItem(
            id = "5ed8da011f071c00465b2027",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372889_m650.jpg",
            price = "259",
            title = "Бургер \"Мексика\"",
            isSale = true
        ),
        DishItem(
            id = "5ed8da011f071c00465b2028",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372890_m650.jpg",
            price = "379",
            title = "Бургер \"Русский\""
        ),
    )

    AppTheme {
        Grid(
            items = dishes,
            cols = 2
        ) {
            ProductItem(dish = it, onToggleLike = {}, onAddToCart = {}, onClick = {})
        }
    }
}