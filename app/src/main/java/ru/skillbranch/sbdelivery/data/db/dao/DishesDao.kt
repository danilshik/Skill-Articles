package ru.skillbranch.sbdelivery.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.sbdelivery.data.db.entity.DishDV
import ru.skillbranch.sbdelivery.data.db.entity.DishLikedPersist
import ru.skillbranch.sbdelivery.data.db.entity.DishItemDV
import ru.skillbranch.sbdelivery.data.db.entity.DishPersist

@Dao
interface DishesDao {
    @Query("SELECT * FROM DishItemDV")
    fun findAllDishes(): Flow<List<DishItemDV>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDishes(dishes: List<DishPersist>)

    @Query("SELECT COUNT(*) FROM dishes")
    suspend fun dishesCounts(): Int

    @Query("SELECT * FROM DishItemDV WHERE title LIKE '%' || :query || '%' AND id IN (:ids) ORDER BY title ASC")
    fun searchDishesByTitle(ids: List<String>, query: String): Flow<List<DishItemDV>>

    @Query("SELECT * FROM DishItemDV WHERE title LIKE '%' || :query || '%' AND is_favorite=1 ORDER BY title ASC")
    fun searchFavoriteDishesByTitle(query: String): Flow<List<DishItemDV>>

    @Query("SELECT * FROM DishDV WHERE id=:id")
    fun findDish(id: String): Flow<DishDV>

    @Query("SELECT * FROM DishItemDV WHERE id IN (:ids)")
    fun findByIds(ids: List<String>): Flow<List<DishItemDV>>

    @Query("DELETE FROM dish_favorite WHERE dish_id =:id")
    suspend fun removeFromFavorite(id: String)

    @Insert
    suspend fun addToFavorite(favorite: DishLikedPersist)

    @Query("SELECT * FROM DishItemDV ORDER BY rating DESC LIMIT 10")
    fun findBest(): Flow<List<DishItemDV>>

    @Query("SELECT * FROM DishItemDV ORDER BY like_count DESC LIMIT 10")
    fun findPopular(): Flow<List<DishItemDV>>

    @Query("SELECT id FROM dishes WHERE category = :category")
    suspend fun findCategoryDishesIds(category: String): List<String>

    @Query("SELECT * FROM DishItemDV WHERE id IN (:ids) ORDER BY rating")
    fun findCategoryDishes(ids: List<String>): Flow<List<DishItemDV>>

    @Query("SELECT * FROM DishItemDV WHERE is_favorite=1 ORDER BY rating")
    fun findFavoriteDishes(): Flow<List<DishItemDV>>

}