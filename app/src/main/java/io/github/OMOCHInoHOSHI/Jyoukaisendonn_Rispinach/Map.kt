package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider

@Composable
fun Map() {

    MapContent()
    SideEffect { Log.d("compose-log", "Map") }
}