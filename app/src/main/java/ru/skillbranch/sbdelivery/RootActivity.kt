package ru.skillbranch.sbdelivery

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.DrawerState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ru.skillbranch.sbdelivery.screens.root.RootViewModel
import ru.skillbranch.sbdelivery.screens.root.logic.Command
import ru.skillbranch.sbdelivery.screens.root.logic.NavCmd
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme
import ru.skillbranch.sbdelivery.screens.root.ui.RootScreen

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {

    private val viewModel: RootViewModel by viewModels()

    @InternalCoroutinesApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.commands
                .collect { handleCommands(it) }
        }

        setContent {
            AppTheme {
                RootScreen(viewModel)
            }
            BackHandler {
                viewModel.navigate(NavCmd.Back)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("RootActivity", "Save instant state")
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    private fun handleCommands(cmd: Command) {
        //Handle Android specific command (Activity.finish, ActivityResult e.t.c "
        Log.e("HANDLE CMD", "$cmd")
        when (cmd) {
            Command.Finish -> finish()
        }
    }
}