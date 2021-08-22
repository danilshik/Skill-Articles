package ru.skillbranch.sbdelivery.repository

import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.data.toDishItem
import ru.skillbranch.sbdelivery.data.toDishPersist
import ru.skillbranch.sbdelivery.screens.dishes.data.DishItem
import java.util.*
import javax.inject.Inject

interface IDishesRepository {
    suspend fun searchDishes(query: String): List<DishItem>
    suspend fun isEmptyDishes(): Boolean
    suspend fun syncDishes()
    suspend fun findDishes(): List<DishItem>
    suspend fun findSuggestions(query: String): Map<String, Int>
    suspend fun addDishToCart(dishId: String)
    suspend fun removeDishFromCart(dishId: String)
    suspend fun cartCount(): Int
}

class DishesRepository @Inject constructor(
    private val api: RestService,
    private val dishesDao: DishesDao,
    private val cartDao: CartDao
) : IDishesRepository {
    override suspend fun searchDishes(query: String): List<DishItem> {
        return if (query.isEmpty()) findDishes()
        else dishesDao.findDishesFrom(query)
            .map { it.toDishItem() }
    }

    override suspend fun isEmptyDishes(): Boolean = dishesDao.dishesCounts() == 0

    override suspend fun syncDishes() {
        val dishes = mutableListOf<DishRes>()
        var offset = 0
        while (true) {
            val res = api.getDishes(offset * 10, 10)
            if (res.isSuccessful) {
                offset++
                dishes.addAll(res.body()!!)
            } else break
        }
        dishesDao.insertDishes(dishes.map { it.toDishPersist() })
    }

    override suspend fun findDishes(): List<DishItem> =
        dishesDao.findAllDishes().map { it.toDishItem() }

    override suspend fun findSuggestions(query: String): Map<String, Int> {
        val dishesList = searchDishes(query)
        return dishesList
            .map{ it.title.replace("[.,!?\"-]".toRegex(), "").lowercase(Locale.getDefault()).split(" ")}
            .flatten().
            filter { it.contains(query, true) }
            .groupingBy { it }.eachCount()
    }

    override suspend fun addDishToCart(dishId: String) {
        val count = cartDao.dishCount(dishId) ?: 0
        if (count > 0) cartDao.updateItemCount(dishId, count.inc())
        else cartDao.addItem(CartItemPersist(dishId = dishId))
    }

    override suspend fun removeDishFromCart(dishId: String) {
        val count = cartDao.dishCount(dishId) ?: 0
        if (count > 0) cartDao.decrementItemCount(dishId)
        else cartDao.removeItem(dishId)
    }

    override suspend fun cartCount(): Int = cartDao.cartCount() ?: 0
}