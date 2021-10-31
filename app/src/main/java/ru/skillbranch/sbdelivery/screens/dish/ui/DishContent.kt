package ru.skillbranch.sbdelivery.screens.dish.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.db.entity.DishDV
import ru.skillbranch.sbdelivery.domain.Dish
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@ExperimentalCoilApi
@Composable
fun DishContent(dish: Dish, accept: (Msg) -> Unit, count: Int) {
    ConstraintLayout {

        val (title, poster, description, price, addBtn, favorite, sale) = createRefs()
        val painter = rememberImagePainter(
            data = dish.image,
            builder = {
                crossfade(true)
                placeholder(R.drawable.img_empty_place_holder)
                error(R.drawable.img_empty_place_holder)
                scale(Scale.FILL)
            }
        )
        Image(
            painter = painter,
            contentDescription = dish.title,
            contentScale = if (painter.state is ImagePainter.State.Success) ContentScale.Crop else ContentScale.Inside,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(poster) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .aspectRatio(1.44f)

        )
        IconButton(
            onClick = { accept(Msg.ToggleLike(dish.id, !dish.isFavorite)) },
            modifier = Modifier.constrainAs(favorite) {
                top.linkTo(parent.top, margin = 8.dp)
                end.linkTo(parent.end)
            },
            content = {
                Icon(
                    tint = if (dish.isFavorite) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.onPrimary,
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = "is favorite"
                )
            })
        if (dish.oldPrice != null) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(74.dp, 24.dp)
                    .background(
                        color = MaterialTheme.colors.secondaryVariant,
                        RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    )
                    .constrainAs(sale) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }) {
                Text(
                    text = "АКЦИЯ",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                )
            }

        }
        Text(
            style = MaterialTheme.typography.h5,
            text = dish.title,
            modifier = Modifier
                .constrainAs(title) {
                    width = Dimension.fillToConstraints
                    top.linkTo(poster.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }

        )
        Text(
            text = dish.description,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(description) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        )


        DishPrice(price = dish.price, oldPrice = dish.oldPrice,
            count = count,
            onIncrement = {
                DishFeature.Msg.IncrementCount
                    .let(Msg::Dish)
                    .also(accept)
            },
            onDecrement = {
                DishFeature.Msg.DecrementCount
                    .let(Msg::Dish)
                    .also(accept)
            },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .constrainAs(price) {
                    top.linkTo(description.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

        TextButton(
            onClick = {
                DishFeature.Msg.AddToCart(dish.id, count)
                    .let(Msg::Dish)
                    .also(accept)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .constrainAs(addBtn) {
                    top.linkTo(price.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(
                "Добавить в корзину ${if (count > 1) "($count)" else ""}",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun Stepper(
    value: Int,
    modifier: Modifier = Modifier,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .border(
                0.dp,
                MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (value > 1) {
            IconButton(
                onClick = { onDecrement() },
                content = {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_remove_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight()
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground
                    )
                    .clipToBounds()
            )
        }

        Text(
            text = "$value",
            fontSize = 24.sp,
            style = TextStyle(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        IconButton(
            onClick = { onIncrement() },
            content = {
                Icon(
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_baseline_add_24),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight()
                .border(
                    0.dp,
                    MaterialTheme.colors.onBackground
                )
                .clipToBounds()
        )
    }
}

@Composable
fun DishPrice(
    price: Int,
    modifier: Modifier = Modifier,
    count: Int = 1,
    oldPrice: Int? = null,
    fontSize: Int = 24,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (oldPrice != null) {
            Text(
                text = "${oldPrice * count} Р",
                color = MaterialTheme.colors.onPrimary,
                textDecoration = TextDecoration.LineThrough,
                style = TextStyle(fontWeight = FontWeight.ExtraLight),
                fontSize = fontSize.sp
            )
            Spacer(modifier = Modifier.width(8.dp))

        }
        Text(
            text = "${price * count} Р",
            color = MaterialTheme.colors.secondary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = fontSize.sp
        )
        Spacer(
            modifier = Modifier
                .defaultMinSize(minWidth = 16.dp)
                .weight(1f)
        )
        Stepper(value = count, onIncrement = onIncrement, onDecrement = onDecrement)
    }
}

@Preview
@Composable
fun StepperPreview() {
    AppTheme {
        Stepper(10, onDecrement = {}, onIncrement = {})
    }
}

@Preview
@Composable
fun PricePreview() {
    AppTheme {
        DishPrice(60, oldPrice = 100, count = 5, onDecrement = {}, onIncrement = {})
    }
}

@Preview
@Composable
fun ContentPreview() {
    AppTheme {
        DishContent(
            dish = Dish(
                id = "0",
                image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
                title = "Бургер \"Америка\"",
                description = "320 г • Котлета из 100% говядины (прожарка medium) на гриле, картофельная булочка на гриле, фирменный соус, лист салата, томат, маринованный лук, жареный бекон, сыр чеддер.",
                price = 100,
                oldPrice = 200,
                rating = 5f,
                isFavorite = false,
            ),
            accept = {},
            count = 5
        )
    }
}

