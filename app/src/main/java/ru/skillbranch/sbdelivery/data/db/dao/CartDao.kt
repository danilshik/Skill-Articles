package ru.skillbranch.sbdelivery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.sbdelivery.data.db.entity.CartItemDV
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist

@Dao
interface CartDao {
    @Query("SELECT * FROM CartItemDV")
    fun findCartItems(): Flow<List<CartItemDV>>

    @Query("SELECT SUM(count) FROM cart_items")
    fun cartCountFlow(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(item: CartItemPersist)

    @Query("DELETE FROM cart_items WHERE dishId = :dishId")
    suspend fun removeItem(dishId: String)

    @Query("UPDATE cart_items SET count = count + 1 WHERE dishId = :dishId")
    suspend fun incrementItemCount(dishId: String)

    @Query("UPDATE cart_items SET count = count - 1 WHERE dishId = :dishId")
    suspend fun decrementItemCount(dishId: String)

    @Query("UPDATE cart_items SET count = :count WHERE dishId = :dishId")
    suspend fun updateItemCount(dishId: String, count:Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT count FROM cart_items WHERE dishId = :dishId")
    suspend fun dishCount(dishId: String): Int?

    @Query("SELECT SUM(count) FROM cart_items")
    suspend fun cartCount(): Int?
}