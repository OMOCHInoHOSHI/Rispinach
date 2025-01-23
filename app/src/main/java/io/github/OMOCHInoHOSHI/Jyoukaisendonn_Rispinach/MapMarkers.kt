package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
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
import java.util.Locale
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.GeocodingResult
import fetchImagesFromFirebaseStorage
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.Color

// マーカーを読み込む関数
@Composable
fun loadMarkers(context: Context, imageViewModel: ImageViewModel): MutableList<MarkerOptions> {
    Log.i("MarkerUtils", "Marker_Start")

    // マーカーのリストを保持するための可変リストを作成
    val markers = remember { mutableListOf<MarkerOptions>() }

    // Google Maps APIキーを取得
    val ApiKey = BuildConfig.MAPS_API_KEY
    val geoApiContext = GeoApiContext.Builder()
        .apiKey(ApiKey)
        .build()

    // 画像データのリストをループして各画像の位置情報を取得
    imageViewModel.pictureName.forEach { imageData ->
        val address = imageData.location
        val Title = imageData.title
        val Snippet = imageData.name
        val Lat = imageData.latitude
        val Lng = imageData.longitude

        if (Lat != null && Lng != null) {
            // 緯度経度が既にある場合
            val marker = MarkerOptions()
                .position(LatLng(Lat, Lng))
                .title(Title)
                .snippet(Snippet)
            markers.add(marker)
        } else {
            // 住所を緯度経度に変換
            val results: Array<GeocodingResult> = GeocodingApi.geocode(geoApiContext, address).await()
            if (results.isNotEmpty()) {
                val location = results[0].geometry.location
                val Lat_l = location.lat
                val Lng_l = location.lng

                // 変換結果をログに出力
                //Log.i("GeocodingResult", "Title: $Title, Address: $address, Lat: $Lat, Lng: $Lng")

                // マーカーオプションを作成
                val marker = MarkerOptions()
                    .position(LatLng(Lat_l, Lng_l))
                    .title(Title)
                    .snippet(Snippet)

                // マーカー情報変数に格納
                markers.add(marker)
            } else {
                // 住所が見つからなかった場合のログ出力
                //Log.e("GeocodingResult", "No results found for address: $address")
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

// マーカー付きマップを表示する関数
@Composable
fun MapMarkers(Lat: Double? = null, Lng: Double? = null, imageViewModel: ImageViewModel = viewModel()) {

//    val locationViewModel = LocationViewModel(context = LocalContext.current)
//
//    // LiveDataを監視
//    val location by locationViewModel.location.observeAsState()
//
//    LaunchedEffect(location) {
//        location?.let {
//            println("ここ緯度経度: ${it.latitude}, ${it.longitude}")
//        }
//    }
//    // 必要に応じて権限リクエストを行う
//    LaunchedEffect(Unit) {
////        locationViewModel.requestLocationPermission(activity)
//        locationViewModel.fusedLocation()
//    }

    Log.i("GoogleMap", "GoogleMap_Start")

    // 現在のコンテキストを取得
    val context = LocalContext.current

    // Firebase Storageからデータを読み込む
    LaunchedEffect(Unit) {
        fetchImagesFromFirebaseStorage { images ->
            // ここで画像データを処理
        }
        // ViewModelを使用して画像データを取得
        imageViewModel.fetchImages()
    }

    // マーカーを読み込む
    val markers = loadMarkers(context, imageViewModel)

    // 地名と緯度経度の対応付け
    val locations = mapOf(
        "札幌" to LatLng(43.061944, 141.348889),  // 札幌市役所
        "東京" to LatLng(35.689501, 139.691722),  // 東京都庁
        "名古屋" to LatLng(35.180202, 136.906144),  // 名古屋県庁
        "大阪" to LatLng(34.6937, 135.5023),      // 大阪府庁
        "福岡" to LatLng(33.5890, 130.4020)       // 福岡市役所
    )
    val defaultPosition = locations["大阪"]!! // 大阪府庁
    val defaultZoom = 13f
    val cameraPositionState = rememberCameraPositionState {
        // Postsからマップを開く場合
        if (Lat != null && Lng != null) {       // クリックした投稿の場所を表示(LatとLngがnull以外の場合)
            val Post_Position = LatLng(Lat, Lng)

            position = CameraPosition.fromLatLngZoom(Post_Position, defaultZoom)

        }
        else{       // デフォルト設定の場所に表示
            position = CameraPosition.fromLatLngZoom(defaultPosition, defaultZoom)
        }
    }

    // CoroutineScopeをrememberで保持
    val coroutineScope = rememberCoroutineScope()

    // マーカーの色を設定
    val color_enemy = BitmapDescriptorFactory.HUE_RED

    // 画面全体を埋めるBoxコンポーネント
    Box(Modifier.fillMaxSize()) {
        // Google Mapを表示
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
                    icon = BitmapDescriptorFactory.defaultMarker(color_enemy)
                )
            }
        }

        // ドロップダウンメニューの状態を管理
        var expanded by remember { mutableStateOf(false) }
        val options = locations.keys.toList()
        var selectedOptionText by remember { mutableStateOf(options[0]) }



        // 画面右上に配置するBoxコンポーネント
        Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) {

            Box(
                modifier = Modifier
                    .size(40.dp) // アイコンのサイズに合わせて調整
                    .clip(CircleShape) // 円形にクリップ
                    .background(Color.White) // 白い背景
                    .align(Alignment.Center) // アイコンと重ねる
            )
            // アイコンボタンを表示
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "その他のオプション")
            }
            // ドロップダウンメニューを表示
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                            // 地名に対応する緯度経度を取得
                            val selectedLocation = locations[option]

                            // マップを移動
                            if (selectedLocation != null) {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedLocation,
                                            10f
                                        ), //ズームレベルも変更
                                        1000 // アニメーション時間（ミリ秒）
                                    )
                                }
                                Log.d("MapContent", "$option が選択されました")
                            }
                        }
                    )
                }
            }
        }
    }

    // マーカーの数をログに出力
    println("危険マーカーの数: ${markers.size}")
    Log.i("GoogleMap", "GoogleMap_Mark_End")
}