package ru.skillbranch.sbdelivery.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.skillbranch.sbdelivery.BuildConfig
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.CategoriesDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.*

@Database(entities = [DishPersist::class, CartItemPersist::class, DishLikedPersist::class, CategoryPersist::class], views = [CartItemDV::class, DishItemDV::class, CategoryItemDV::class, DishDV::class], version = AppDb.DATABASE_VERSION, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }
    abstract fun dishesDao(): DishesDao
    abstract fun cartDao(): CartDao
    abstract fun categoryDao(): CategoriesDao
}
