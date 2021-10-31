package ru.skillbranch.sbdelivery.screens.root

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.skillbranch.sbdelivery.screens.root.logic.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class RootViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val dispatcher: EffectDispatcher
) : ViewModel() {
    private val rootFeature: RootFeature = RootFeature(handle.get<RootState>("state"))
    val state
        get() = rootFeature.state
    val commands
        get() = dispatcher.commands

    val notifications
        get() = dispatcher.notifications

    init {
        Log.e("RootViewModel", "${handle.get<RootState>("state")}")
        rootFeature.listen(viewModelScope, dispatcher, handle.get<RootState>("state"))
    }

    fun navigate(cmd: NavCmd) {
        rootFeature.mutate(Msg.Navigate(cmd))
    }

    fun accept(msg: Msg) {
        rootFeature.mutate(msg)
    }

    fun saveState(){

        handle.set("state", state.value)
        Log.e("RootViewModel", "Save state ${handle.get<RootState>("state")}")
    }

}
