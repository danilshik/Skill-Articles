package ru.skillbranch.sbdelivery.screens.home.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import ru.skillbranch.sbdelivery.domain.DishItem
import ru.skillbranch.sbdelivery.screens.components.Grid
import ru.skillbranch.sbdelivery.screens.components.items.ProductItem
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@ExperimentalCoilApi
@Composable
fun SectionList(
    dishes: List<DishItem>,
    title: String,
    modifier: Modifier = Modifier,
    limit: Int = 6,
    onClick: (DishItem) -> Unit,
    onAddToCart: (DishItem) -> Unit,
    onToggleLike: (DishItem) -> Unit,
) {
    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = if (!expanded) "См. все" else "Свернуть",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(all = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!expanded) {
            //recycler view alternative in compose (lazy row, lazy column)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                items(dishes.take(limit), { it.id }) { item ->
                    ProductItem(
                        dish = item,
                        onToggleLike = onToggleLike,
                        onAddToCart = onAddToCart,
                        onClick = onClick
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        } else {
            //static grid
            Grid(
                items = dishes,
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp)
            ) {
                ProductItem(
                    dish = it,
                    onToggleLike = onToggleLike,
                    onAddToCart = onAddToCart,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ShimmerProductItem(
    colors: List<Color>,
    xShimmer: Float,
    yShimmer: Float,
    cardWidth: Dp,
    gradientWidth: Float,
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(
        colors,
        start = Offset(xShimmer - gradientWidth, yShimmer - gradientWidth),
        end = Offset(xShimmer, yShimmer)
    )

    Card(modifier = modifier.width(cardWidth)) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(18.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Spacer(
                    modifier = Modifier
                        .height(14.dp)
                        .width(cardWidth * 0.35f)
                        .background(
                            brush = brush,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    modifier = Modifier
                        .height(14.dp)
                        .width(cardWidth * 0.85f)
                        .background(
                            brush = brush,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Spacer(
                    modifier = Modifier
                        .height(14.dp)
                        .width(cardWidth * 0.55f)
                        .background(
                            brush = brush,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ShimmerSection(
    itemWidth:Dp,
    title: String,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.weight(1f))
            Text(text =  "См. все" ,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .padding(all = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWithPx = with(LocalDensity.current){ itemWidth.toPx() }
            val cardHeightPx = with(LocalDensity.current){ (itemWidth/0.68f).toPx() }
            val gradientWidth: Float = (0.4f * cardHeightPx)

            val infinityTransition = rememberInfiniteTransition()

            val xCardShimmer by infinityTransition.animateFloat(
                initialValue = 0f,
                targetValue = (cardWithPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                        delayMillis = 300
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val yCardShimmer by infinityTransition.animateFloat(
                initialValue = 0f,
                targetValue = (cardHeightPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                        delayMillis = 300
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val colors = listOf(
                MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                MaterialTheme.colors.onBackground.copy(alpha = 0.2f),
                MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                item{
                    Spacer(modifier = Modifier.width(8.dp))
                }
                items(5){
                    ShimmerProductItem(
                        colors = colors,
                        xShimmer = xCardShimmer,
                        yShimmer = yCardShimmer,
                        cardWidth = itemWidth,
                        gradientWidth = gradientWidth
                    )
                }
                item{
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ShimmerSectionPreview() {
    AppTheme {
        ShimmerSection(itemWidth = 160.dp, title = "Популярное")
    }
}



@Preview
@Composable
fun ShimmerPreview() {
    AppTheme {
        val colors = listOf(
            MaterialTheme.colors.onBackground.copy(alpha = .4f),
            MaterialTheme.colors.onBackground.copy(alpha = .2f),
            MaterialTheme.colors.onBackground.copy(alpha = .4f),
        )
        ShimmerProductItem(colors, 100f, 600f, 160.dp, 100f)
    }

}


@Preview
@Composable
fun SectionPreview() {
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
        DishItem(
            id = "5ed8da011f071c00465b2029",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372891_m650.jpg",
            price = "189",
            title = "Бургер \"Люксембург\""
        ),
        DishItem(
            id = "5ed8da011f071c00465b202a",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372893_m650.jpg",
            price = "199",
            title = "Бургер \"Классика\""
        ),
        DishItem(
            id = "5ed8da011f071c00465b202b",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312700349_m650.jpg",
            price = "279",
            title = "Бургер \"Швейцария\""
        ),
        DishItem(
            id = "5ed8da011f071c00465b202c",
            image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312700351_m650.jpg",
            price = "289",
            title = "Бургер \"Франция\""
        ),
    )

    AppTheme {
        SectionList(
            dishes = dishes,
            title = "Рекомендуем",
            onClick = {},
            onAddToCart = {}
        ) {}
    }

}