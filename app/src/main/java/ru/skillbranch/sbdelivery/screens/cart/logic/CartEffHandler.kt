package ru.skillbranch.sbdelivery.screens.cart.logic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.skillbranch.sbdelivery.repository.CartRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class CartEffHandler @Inject constructor(
    val repository: CartRepository,
    private val notifyChanel: Channel<Eff.Notification>,
    override var localJob: Job
) : IEffectHandler<CartFeature.Eff, Msg> {

    private val errHandler = CoroutineExceptionHandler{_, t ->
        t.printStackTrace()
        t.message?.let { notifyChanel.trySend(Eff.Notification.Error(it)) }
    }

    override suspend fun handle(eff: CartFeature.Eff, commit: (Msg) -> Unit) {
        CoroutineScope(coroutineContext + localJob + errHandler).launch {

            when (eff) {
                is CartFeature.Eff.LoadCart -> {

//                    val cart = repository.loadItems()         //suspend load items
//                    val msg = CartFeature.Msg.ShowCart(cart)  //items transform to CartFeature.Msg
//                    val rootMsg  = Msg.Cart(msg)              //transform local msg to Msg.Cart - root msg
//                    commit(rootMsg)                           //commit state changes

                    repository.loadItems()                      //load flow
                        .map(CartFeature.Msg::ShowCart)         //items transform to CartFeature.Msg
                        .map (Msg::Cart)                        //transform local msg to Msg.Cart - root msg
                        .collect { commit(it) }                 //commit state changes
                }

                is CartFeature.Eff.DecrementItem -> repository.decrementItem(eff.dishId)
                is CartFeature.Eff.IncrementItem -> repository.incrementItem(eff.dishId)
                is CartFeature.Eff.RemoveItem -> repository.removeItem(eff.dishId)
                is CartFeature.Eff.SendOrder -> {
                    repository.clearCart()
                    notifyChanel.send(Eff.Notification.Text("Заказ оформлен"))
                }
            }
        }
    }
}