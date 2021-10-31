package ru.skillbranch.sbdelivery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
class CategoryPersist(
    @PrimaryKey
    val id: String,
    val title: String,
    val icon: String?,
    @ColumnInfo(name = "category_order")
    val order: Int,
    val parent: String?,
    val active: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@DatabaseView(
    """
        SELECT id, title, icon, category_order, parent FROM categories 
    """
)
data class CategoryItemDV(
    val id: String,
    val title: String,
    val icon: String?,
    @ColumnInfo(name = "category_order")
    val order: Int,
    @ColumnInfo(name = "parent")
    val parentId: String?
)