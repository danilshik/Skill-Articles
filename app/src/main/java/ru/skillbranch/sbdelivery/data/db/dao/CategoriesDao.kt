package ru.skillbranch.sbdelivery.data.db.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.sbdelivery.data.db.entity.CategoryItemDV
import ru.skillbranch.sbdelivery.data.db.entity.CategoryPersist

@Dao
interface CategoriesDao {
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun categoriesCounts(): Int

    @Insert(onConflict = REPLACE)
    suspend fun insertCategories(map: List<CategoryPersist>)

    @Query("SELECT * FROM CategoryItemDV")
    fun findCategories(): Flow<List<CategoryItemDV>>


    @Query("SELECT * FROM CategoryItemDV")
    suspend fun findCat(): List<CategoryItemDV>
}