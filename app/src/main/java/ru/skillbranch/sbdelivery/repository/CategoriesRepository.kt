package ru.skillbranch.sbdelivery.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.skillbranch.sbdelivery.data.db.dao.CategoriesDao
import ru.skillbranch.sbdelivery.data.toCategoryItem
import ru.skillbranch.sbdelivery.domain.CategoryItem
import javax.inject.Inject

interface ICategoriesRepository {
    fun findCategories(): Flow<List<CategoryItem>>
}

class CategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) : ICategoriesRepository {
    override fun findCategories(): Flow<List<CategoryItem>> = categoriesDao.findCategories()
        .map { dv -> dv.map { it.toCategoryItem() } }
}
