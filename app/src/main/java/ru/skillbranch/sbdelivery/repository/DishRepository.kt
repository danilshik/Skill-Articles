package ru.skillbranch.sbdelivery.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.db.entity.DishDV
import ru.skillbranch.sbdelivery.data.db.entity.DishLikedPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.req.ReviewReq
import ru.skillbranch.sbdelivery.data.network.res.ReviewRes
import ru.skillbranch.sbdelivery.data.toDish
import ru.skillbranch.sbdelivery.domain.Dish
import javax.inject.Inject

interface IDishRepository {
    suspend fun findDish(id: String): Flow<Dish>
    suspend fun addToCart(id: String, count: Int)
    suspend fun cartCount(): Int
    suspend fun loadReviews(dishId: String): List<ReviewRes>
    suspend fun sendReview(id: String, rating: Int, review: String): ReviewRes
    suspend fun insertFavorite(id: String)
    suspend fun removeFavorite(id: String)
}

class DishRepository @Inject constructor(
    private val api: RestService,
    private val dishesDao: DishesDao,
    private val cartDao: CartDao,
) : IDishRepository {

    override suspend fun findDish(id: String): Flow<Dish> = dishesDao.findDish(id)
        .map { it.toDish() }

    override suspend fun addToCart(id: String, count: Int) {
        val cartCount = cartDao.dishCount(id) ?: 0
        if (cartCount > 0) cartDao.updateItemCount(id, cartCount + count)
        else cartDao.addItem(CartItemPersist(dishId = id, count = count))
    }

    override suspend fun cartCount()= cartDao.cartCount() ?: 0
    override suspend fun loadReviews(dishId: String): List<ReviewRes> {

        val reviews = mutableListOf<ReviewRes>()
        var offset = 0
        while (true) {
            val res = api.getReviews(dishId, offset * 10, 10)
            if (res.isSuccessful) {
                offset++
                reviews.addAll(res.body()!!)
            } else break
        }

        return reviews
    }

    override suspend fun sendReview(id: String, rating: Int, review: String) =
        api.sendReview(id, ReviewReq(rating, text = review))

    override suspend fun insertFavorite(id: String) = dishesDao.addToFavorite(DishLikedPersist(id))

    override suspend fun removeFavorite(id: String) = dishesDao.removeFromFavorite(id)

}