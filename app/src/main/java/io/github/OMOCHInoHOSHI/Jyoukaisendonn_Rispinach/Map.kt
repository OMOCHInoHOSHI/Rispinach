package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
fun Map()
{
//    MapContent()      // コメントアウト(中村)
    MapMarkers()        // マーカー付き地図
    SideEffect { Log.d("compose-log", "Map") }
    //デバッグ用
//    Text("main/map")
    println("map")
}