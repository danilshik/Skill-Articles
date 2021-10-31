package ru.skillbranch.sbdelivery.repository

import kotlinx.coroutines.flow.*
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.CategoriesDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.db.entity.DishLikedPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.data.network.res.toCategoryPersist
import ru.skillbranch.sbdelivery.data.toDishPersist
import javax.inject.Inject

interface IRootRepository {
    fun cartCount(): Flow<Int>
    suspend fun isEmptyDishes(): Boolean
    suspend fun syncDishes()
    suspend fun addDishToCart(id: String)
    suspend fun removeDishFromCart(dishId: String)
}

class RootRepository @Inject constructor(
    private val api: RestService,
    private val cartDao: CartDao,
    private val dishesDao: DishesDao,
    private val categoriesDao: CategoriesDao
) : IRootRepository {
    override fun cartCount() = cartDao.cartCountFlow()
        .flatMapLatest { flowOf(it ?: 0) }
        .distinctUntilChanged()

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
        dishes.map { it.toDishPersist() }
            .also { dishesDao.insertDishes(it) }
    }

    suspend fun isEmptyCategories(): Boolean = categoriesDao.categoriesCounts() == 0

    suspend fun syncCategories() = api.getCategories()
            .map { it.toCategoryPersist() }
            .also {  categoriesDao.insertCategories(it) }

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

    suspend fun insertFavorite(id: String) = dishesDao.addToFavorite(DishLikedPersist(id))
    suspend fun removeFavorite(id: String) = dishesDao.removeFromFavorite(id)


}