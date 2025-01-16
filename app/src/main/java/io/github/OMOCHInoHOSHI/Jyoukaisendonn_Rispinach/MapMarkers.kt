package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun loadMarkers(context: Context): Pair<MutableList<MarkerOptions>, MutableList<MarkerOptions>> {
    Log.i("MarkerUtils", "Marker_Start")

    val markers_enemy = remember { mutableListOf<MarkerOptions>() }  // マーカーのリストを保持するための可変リストを作成
    val markers_safe = remember { mutableListOf<MarkerOptions>() }  // マーカーのリストを保持するための可変リストを作成

    // markersが空の場合のみファイルを読み込む
    if (markers_enemy.size <= 0) {

        // 指定されたファイル名のアセットファイルを開く
        val inputStream = context.assets.open("Mark.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))

        // ファイルの各行を読み込む
        reader.useLines { lines ->
            lines.forEach { line ->
                // 行をカンマで分割して、緯度、経度、タイトルを取得
                val parts = line.split(",")
                if (parts.size >= 4) {
                    val Lat = parts[0].toDoubleOrNull() // 緯度を取得
                    val Lng = parts[1].toDoubleOrNull() // 経度を取得
                    val Title = parts[2] // タイトルを取得
                    val Snippet = parts[3]  // 危険生物かどうか

                    // 情報をリストにまとめる
                    if (Lat != null && Lng != null) {
                        //val color = BitmapDescriptorFactory.HUE_BLUE
                        Log.i("MarkerUtils", "Marker_center")

                        val marker = MarkerOptions()
                            .position(LatLng(Lat, Lng))
                            .title(Title)
                            .snippet(Snippet)

                        if (Snippet == "enemy") {
                            markers_enemy.add(marker)
                        } else if (Snippet == "safe") {
                            markers_safe.add(marker)
                        }
                    }
                }
            }
        }
    }

    // markersの中身をログに出力
    markers_safe.forEach { markerOptions ->
        Log.i("MarkerUtils", "Safe Marker: ${markerOptions.position}, Title: ${markerOptions.title}")
    }

    Log.i("MarkerUtils", "Marker_End")
    return Pair(markers_enemy, markers_safe)
}


@Composable
fun MapMarkers() {
    Log.i("GoogleMap", "GoogleMap_Start")

    // Mark.txtファイルからマーカー情報を読み込む
    val context = LocalContext.current
    val (markers_enemy, markers_safe) = loadMarkers(context)

    val defaultPosition = LatLng(35.689501, 139.691722) // 東京都庁
    val defaultZoom = 8f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, defaultZoom)
    }

    val color_enemy = BitmapDescriptorFactory.HUE_RED
    val color_safe = BitmapDescriptorFactory.HUE_GREEN

    Log.i("GoogleMap", "GoogleMap_Mark_Start")
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        // 読み込んだマーカー情報をマップに追加
        markers_enemy.forEach { markerOptions ->
            Marker(
                state = rememberMarkerState(position = markerOptions.position),
                title = markerOptions.title,
                snippet = markerOptions.snippet,
                icon = BitmapDescriptorFactory.defaultMarker(color_enemy)
            )
        }
        markers_safe.forEach { markerOptions ->
            Marker(
                state = rememberMarkerState(position = markerOptions.position),
                title = markerOptions.title,
                snippet = markerOptions.snippet,
                icon = BitmapDescriptorFactory.defaultMarker(color_safe)
            )
        }
    }

    // マーカーの数をログに出力
    println("危険マーカーの数: ${markers_enemy.size}")
    println("安全マーカーの数: ${markers_safe.size}")
    Log.i("GoogleMap", "GoogleMap_Mark_End")
}