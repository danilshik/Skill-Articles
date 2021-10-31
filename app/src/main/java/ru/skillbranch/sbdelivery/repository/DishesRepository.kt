package ru.skillbranch.sbdelivery.repository

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.data.toDishItem
import ru.skillbranch.sbdelivery.data.toDishPersist
import ru.skillbranch.sbdelivery.domain.DishItem
import javax.inject.Inject

interface IDishesRepository {
    suspend fun addDishToCart(id: String)
    suspend fun removeDishFromCart(dishId: String)
    suspend fun cartCount(): Int
    suspend fun findSuggestions(category: String, query: String): Flow<Map<String, Int>>
    suspend fun searchDishes(category: String, query: String): Flow<List<DishItem>>
    fun findRecommended(ids: List<String>): Flow<List<DishItem>>
    fun findBest(): Flow<List<DishItem>>
    fun findPopular(): Flow<List<DishItem>>
    fun findFavoriteSuggestions(query: String): Flow<Map<String, Int>>
    fun findFavoriteDishes(): Flow<List<DishItem>>
    fun searchFavoriteDishes(query: String): Flow<List<DishItem>>
    suspend fun findDishesByCategory(category: String): Flow<List<DishItem>>
    suspend fun getRecommended(): List<String>
    suspend fun syncRecommended(ids: List<String>): List<DishItem>
}

class DishesRepository @Inject constructor(
    private val api: RestService,
    private val dishesDao: DishesDao,
    private val cartDao: CartDao
) : IDishesRepository {

    override suspend fun searchDishes(category: String, query: String): Flow<List<DishItem>> {
        return if (query.isEmpty()) findDishesByCategory(category)
        else {
            val ids = dishesDao.findCategoryDishesIds(category)
            dishesDao.searchDishesByTitle(ids, query)
                .map { dv -> dv.map { it.toDishItem() } }
        }

    }

    override suspend fun findDishesByCategory(category: String): Flow<List<DishItem>> {
        val ids = dishesDao.findCategoryDishesIds(category)
        return dishesDao.findCategoryDishes(ids)
            .map { dv -> dv.map { it.toDishItem() } }
    }

    override fun searchFavoriteDishes(query: String): Flow<List<DishItem>> {
        return if (query.isEmpty()) findFavoriteDishes()
        else dishesDao.searchFavoriteDishesByTitle(query)
            .map { dv -> dv.map { it.toDishItem() } }

    }

    override fun findFavoriteDishes(): Flow<List<DishItem>> = dishesDao.findFavoriteDishes()
        .map { dv -> dv.map { it.toDishItem() } }

    override suspend fun findSuggestions(category: String, query: String): Flow<Map<String, Int>> {
        return if (query.isEmpty()) flowOf(emptyMap())
        else searchDishes(category, query)
            .map { toSuggestions(it, query) }
    }

    override fun findFavoriteSuggestions(query: String): Flow<Map<String, Int>> {
        return if (query.isEmpty()) flowOf(emptyMap())
        else searchFavoriteDishes(query)
            .map { toSuggestions(it, query) }
    }

    override suspend fun addDishToCart(id: String) {
        val count = cartDao.dishCount(id) ?: 0
        if (count > 0) cartDao.updateItemCount(id, count.inc())
        else cartDao.addItem(CartItemPersist(dishId = id))
    }

    override suspend fun removeDishFromCart(dishId: String) {
        val count = cartDao.dishCount(dishId) ?: 0
        if (count > 1) cartDao.decrementItemCount(dishId)
        else cartDao.removeItem(dishId)
    }

    override suspend fun cartCount(): Int = cartDao.cartCount() ?: 0

    override suspend fun getRecommended(): List<String> = api.getRecommended().body()!!

    override suspend fun syncRecommended(ids: List<String>): List<DishItem> {
        val dishes = mutableListOf<DishRes>()
        coroutineScope {
            ids.forEach { id ->
                launch {
                    api.getDish(id).body()!!.also { res -> dishes.add(res) }
                }
            }
        }
        return dishes.map { it.toDishPersist() }
            .also { dishesDao.insertDishes(it) }
            .map { it.toDishItem() }
    }

    override fun findRecommended(ids: List<String>): Flow<List<DishItem>> = dishesDao.findByIds(ids)
        .distinctUntilChanged()
        .map { dv -> dv.map { it.toDishItem() } }

    override fun findBest(): Flow<List<DishItem>> = dishesDao.findBest()
        .distinctUntilChanged()
        .map { dv -> dv.map { it.toDishItem() } }

    override fun findPopular(): Flow<List<DishItem>> = dishesDao.findPopular()
        .distinctUntilChanged()
        .map { dv -> dv.map { it.toDishItem() } }

    private fun toSuggestions(list: List<DishItem>, query: String) = list
        .map { it.title.replace(Regex("[.,!?\"-]"), "") }
        .flatMap { it.split(" ") }
        .filter { it.contains(query, true) }
        .groupingBy { it.toLowerCase() }
        .eachCount()
        .toList()
        .sortedByDescending { (_, count) -> count }
        .take(5)
        .toMap()
}
