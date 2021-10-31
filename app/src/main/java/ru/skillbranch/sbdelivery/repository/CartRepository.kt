package ru.skillbranch.sbdelivery.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.toCartItem
import ru.skillbranch.sbdelivery.domain.CartItem
import javax.inject.Inject

interface ICartRepository {
    fun loadItems(): Flow<List<CartItem>>
    suspend fun incrementItem(dishId: String)
    suspend fun decrementItem(dishId: String)
    suspend fun removeItem(dishId: String)
    suspend fun clearCart()
}

class CartRepository @Inject constructor(
    private val api: RestService,
    private val cartDao: CartDao
) : ICartRepository {
    override fun loadItems(): Flow<List<CartItem>> = cartDao.findCartItems()
        .map { dv -> dv.map { it.toCartItem() } }

    override suspend fun incrementItem(dishId: String) {
        cartDao.incrementItemCount(dishId)
    }

    override suspend fun decrementItem(dishId: String) {
        cartDao.decrementItemCount(dishId)
    }

    override suspend fun removeItem(dishId: String) {
        cartDao.removeItem(dishId)
    }

    override suspend fun clearCart() = cartDao.clearCart()
}