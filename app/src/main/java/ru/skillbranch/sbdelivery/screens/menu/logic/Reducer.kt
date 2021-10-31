package ru.skillbranch.sbdelivery.screens.menu.logic

import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.NavCmd
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

fun Set<MenuFeature.Eff>.toEffs(): Set<Eff> = mapTo(HashSet(), Eff::Menu)

fun MenuFeature.State.reduce(
    msg: MenuFeature.Msg,
    rootState: RootState
): Pair<RootState, Set<Eff>> {
    val (screenState, effs) = selfReduce(msg)
    return rootState.changeCurrentScreen<ScreenState.Menu> { copy(state = screenState) } to effs
}

fun MenuFeature.State.selfReduce(msg: MenuFeature.Msg): Pair<MenuFeature.State, Set<Eff>> =
    when (msg) {
        is MenuFeature.Msg.ClickCategory -> {
            val nextParent = categories.find { it.parentId == msg.id } //if has child category level
            if(nextParent == null) copy() to setOf(Eff.Nav(NavCmd.ToCategory(msg.id, msg.title))) //if not has child level then open category
            else copy(parentId = msg.id) to emptySet() //else has child subcategory then update current
        }
        MenuFeature.Msg.PopCategory -> {
            copy(parentId = parent?.parentId) to emptySet()
        }
        is MenuFeature.Msg.ShowMenu -> copy( categories =  msg.categories) to emptySet()
    }

