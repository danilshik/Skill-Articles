package ru.skillbranch.sbdelivery.ui.search

import ru.skillbranch.sbdelivery.core.adapter.ProductItemState

//data class SearchState(val items: List<ProductItemState>)
sealed class SearchState {
    data class Result(val items: List<ProductItemState>) : SearchState()
    object Loading : SearchState()
    data class Error(val errorDescription: String, val emptyLIst: Boolean = false) : SearchState()
}