package ru.skillbranch.sbdelivery.screens.dish.logic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.skillbranch.sbdelivery.repository.DishRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject

class DishEffHandler @Inject constructor(
    private val repository: DishRepository,
    private val notifyChannel: Channel<Eff.Notification>,
    private val dispatcher: CoroutineDispatcher  = Dispatchers.Default
) :
    IEffectHandler<DishFeature.Eff, Msg> {

    private var localJob: Job? = null

    override suspend fun handle(effect: DishFeature.Eff, commit: (Msg) -> Unit) {

        if (localJob == null) localJob = Job()
        withContext(localJob!! + dispatcher) {
            when (effect) {
                is DishFeature.Eff.AddToCart -> {
                    repository.addToCart(effect.id, effect.count)
                    notifyChannel.send(Eff.Notification.Text("В корзмну добавлено ${effect.count} товаров"))
                }
                is DishFeature.Eff.LoadDish -> {
                    val dish = repository.findDish(effect.dishId)
                    commit(DishFeature.Msg.ShowDish(dish).toMsg())
                }
                is DishFeature.Eff.LoadReviews -> {
                    try {
                        val reviews = repository.loadReviews(effect.dishId)
                        commit(DishFeature.Msg.ShowReviews(reviews).toMsg())
                    } catch (t: Throwable) {
                        notifyChannel.send(Eff.Notification.Error(t.message ?: "something error"))
                    }
                }
                is DishFeature.Eff.SendReview -> {
                    val res = repository.sendReview(
                        id = effect.id,
                        rating = effect.rating,
                        review = effect.review
                    )
                    notifyChannel.send(Eff.Notification.Text("Отзыв успешно отправлен"))
                }
                is DishFeature.Eff.SetLike -> {
                    repository.setLike(effect.id, effect.isLike)
                }
                is DishFeature.Eff.Terminate -> {
                    localJob?.cancel("Terminate coroutine scope")
                    localJob = null
                }
            }
        }

    }

    private fun DishFeature.Msg.toMsg(): Msg = Msg.Dish(this)

}



