package ru.skillbranch.sbdelivery.core.notifier

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.ReplaySubject
import ru.skillbranch.sbdelivery.core.notifier.event.BasketEvent

class BasketNotifierImpl : BasketNotifier {

    private val notifier = ReplaySubject.create<BasketEvent>()

    override fun eventSubscribe(): Observable<BasketEvent> {
        return notifier.hide()
    }

    override fun putDishes(dish: BasketEvent.AddDish) {
        notifier.onNext(dish)
    }


}