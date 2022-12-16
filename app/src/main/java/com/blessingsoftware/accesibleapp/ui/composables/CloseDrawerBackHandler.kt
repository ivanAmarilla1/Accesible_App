package com.blessingsoftware.accesibleapp.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CloseDrawerBackHandler(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    onElse: @Composable () -> Unit
) {
    if (scaffoldState.drawerState.isOpen) {
        BackHandler(enabled = true, onBack = {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        })
    } else run {
        onElse()
    }
}
