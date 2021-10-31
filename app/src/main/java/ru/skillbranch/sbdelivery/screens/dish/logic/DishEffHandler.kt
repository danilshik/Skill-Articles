package ru.skillbranch.sbdelivery.screens.dish.logic

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import ru.skillbranch.sbdelivery.repository.DishRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class DishEffHandler @Inject constructor(
    private val repository: DishRepository,
    private val notifyChanel: Channel<Eff.Notification>,
    override var localJob: Job
) : IEffectHandler<DishFeature.Eff, Msg> {

    private val errHandler = CoroutineExceptionHandler{_, t ->
        t.printStackTrace()
        t.message?.let { notifyChanel.trySend(Eff.Notification.Error(it)) }
    }

    override suspend fun handle(eff: DishFeature.Eff, commit: (Msg) -> Unit) {
        CoroutineScope(coroutineContext + localJob + errHandler).launch {
            when (eff) {
                is DishFeature.Eff.LoadDish -> {
                    repository.findDish(eff.dishId)
                        .map(DishFeature.Msg::ShowDish)
                        .map(Msg::Dish)
                        .collect { commit(it) }
                }

                is DishFeature.Eff.AddToCart -> {
                    repository.addToCart(eff.id, eff.count)
                    repository.cartCount()
                        .let(Msg::UpdateCartCount)
                        .also(commit)
                    notifyChanel.send(Eff.Notification.Text("В корзину добавлено ${eff.count} товаров"))
                }

                is DishFeature.Eff.LoadReviews -> {
                    repository.loadReviews(eff.dishId)
                        .let(DishFeature.Msg::ShowReviews)
                        .let(Msg::Dish)
                        .also(commit)
                }

                is DishFeature.Eff.SendReview -> {
                    val review = repository.sendReview(eff.id, eff.rating, eff.review)

                    repository.loadReviews(eff.id).plus(review)
                        .let(DishFeature.Msg::ShowReviews)
                        .let(Msg::Dish)
                        .also(commit)
                    notifyChanel.send(Eff.Notification.Text("Отзыв успешно отправлен"))
                }
            }
        }

    }
}



