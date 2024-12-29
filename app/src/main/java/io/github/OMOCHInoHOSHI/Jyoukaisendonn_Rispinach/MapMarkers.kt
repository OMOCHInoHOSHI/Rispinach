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
fun loadMarkers(context: Context): List<MarkerOptions> {
    Log.i("MarkerUtils", "Marker_Start")

    val markers = remember { mutableListOf<MarkerOptions>() }  // マーカーのリストを保持するための可変リストを作成

    // markersが空の場合のみファイルを読み込む
    if (markers.size <= 0) {

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

                        // 緯度と経度が有効な場合、MarkerOptionsを作成してリストに追加
                        markers.add(
                            MarkerOptions()
                                .position(LatLng(Lat, Lng))
                                .title(Title)
                                .snippet(Snippet)
                                //.icon(BitmapDescriptorFactory.defaultMarker(color))
                        )
                    }
                }
            }
        }
    }

    // markersの中身をログに出力
    markers.forEach { markerOptions ->
        Log.i("MarkerUtils", "Marker: ${markerOptions.position}, Title: ${markerOptions.title}")
    }

    Log.i("MarkerUtils", "Marker_End")
    return markers
}

@Composable
fun MapMarkers() {
    Log.i("GoogleMap", "GoogleMap_Start")

    // Mark.txtファイルからマーカー情報を読み込む
    val context = LocalContext.current
    val markers: List<MarkerOptions>
    markers = loadMarkers(context)

    val defaultPosition = LatLng(35.689501, 139.691722) // 東京都庁
    val defaultZoom = 8f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, defaultZoom)
    }
    val color = BitmapDescriptorFactory.HUE_BLUE

    Log.i("GoogleMap", "GoogleMap_Mark_Start")
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        // 読み込んだマーカー情報をマップに追加
        markers.forEach { markerOptions ->
            Marker(
                state = rememberMarkerState(position = markerOptions.position),
                title = markerOptions.title,
                snippet = markerOptions.snippet,
                icon = (BitmapDescriptorFactory.defaultMarker(color)) // マーカーの色を設定
            )
        }
    }

    // マーカーの数をログに出力
    println("現在のマーカーの数: ${markers.size}")
    Log.i("GoogleMap", "GoogleMap_Mark_End")
}