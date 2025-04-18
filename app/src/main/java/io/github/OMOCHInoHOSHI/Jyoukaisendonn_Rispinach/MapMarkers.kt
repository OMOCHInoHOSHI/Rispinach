package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Color
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import fetchImagesFromFirebaseStorage
import kotlinx.coroutines.launch

// マーカーを読み込む関数
@Composable
fun loadMarkers(context: Context, imageViewModel: ImageViewModel): MutableList<MarkerOptions> {
    Log.i("MarkerUtils", "Marker_Start")

    // マーカーのリストを保持するための可変リストを作成
//    val markers = remember { mutableListOf<MarkerOptions>() }

    // マーカーのリストを再作成
    val markers = mutableListOf<MarkerOptions>()

//    // Google Maps APIキーを取得
//    val ApiKey = BuildConfig.MAPS_API_KEY
//    val geoApiContext = GeoApiContext.Builder()
//        .apiKey(ApiKey)
//        .build()

    // 画像データのリストをループして各画像の位置情報を取得
    imageViewModel.pictureName.forEach { imageData ->
//        val address = imageData.location
        val Title = imageData.title
        val Snippet = "AI判定：" + imageData.name
        val Lat = imageData.latitude
        val Lng = imageData.longitude
        val bitmap = imageData.bitmap

        // ビットマップをリサイズして白い枠と逆三角形を追加(色は別々にすること!!)
        val resizedBitmap = ResizeMarkerIcon(bitmap, 140, 10, "#ed6d35", "#ed6d36") // 適切なサイズに変更(色：キャロットオレンジ、ほぼキャロットオレンジ)

        if (Lat != null && Lng != null) {
            // 緯度経度が既にある場合
            val marker = MarkerOptions()
                .position(LatLng(Lat, Lng))
                .title(Title)
                .snippet(Snippet)
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)) // カスタムアイコンを設定

            markers.add(marker)
        }
//        else {
//            // 住所を緯度経度に変換
//            val results: Array<GeocodingResult> = GeocodingApi.geocode(geoApiContext, address).await()
//            if (results.isNotEmpty()) {
//                val location = results[0].geometry.location
//                val Lat_l = location.lat
//                val Lng_l = location.lng
//
//                // 変換結果をログに出力
//                //Log.i("GeocodingResult", "Title: $Title, Address: $address, Lat: $Lat, Lng: $Lng")
//
//                // マーカーオプションを作成
//                val marker = MarkerOptions()
//                    .position(LatLng(Lat_l, Lng_l))
//                    .title(Title)
//                    .snippet(Snippet)
//                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)) // カスタムアイコンを設定
//
//                // マーカー情報変数に格納
//                markers.add(marker)
//            }
//            else {
//                // 住所が見つからなかった場合のログ出力
//                //Log.e("GeocodingResult", "No results found for address: $address")
//            }
//        }
    }

    // markersの中身をログに出力
    markers.forEach { markerOptions ->
        Log.i("MarkerUtils", "Marker: ${markerOptions.position}, Title: ${markerOptions.title}")
    }

    Log.i("MarkerUtils", "Marker_End")
    return markers
}

// マーカー付きマップを表示する関数
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapMarkers(Lat: Double? = null, Lng: Double? = null, mapL: Boolean = false, imageViewModel: ImageViewModel = viewModel()) {

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

    // ロケーション用
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(context)
    )

    // (2) 位置情報の権限リクエストと、位置情報の取得開始を行う
    LaunchedEffect(Unit) {

        locationViewModel.fusedLocation()
    }

    // (3) 現在地のLiveDataを観測
    val currentLocation by locationViewModel.location.observeAsState()

    // 緯度と経度を個別の変数に格納
    val latitude = currentLocation?.latitude    // 緯度
    val longitude = currentLocation?.longitude  // 経度

    // 取得できたか確認
    println("latitude = $latitude")
    println("longitude = $longitude")



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

    // ロケーションリスト
    var locations: Map<String, LatLng>

    // デフォルトの位置
    var defaultPosition:LatLng

    if(currentLocation == null){
        locations = mapOf(
            "札幌" to LatLng(43.061944, 141.348889),  // 札幌市役所
            "東京" to LatLng(35.689501, 139.691722),  // 東京都庁
            "名古屋" to LatLng(35.180202, 136.906144),  // 名古屋県庁
            "大阪" to LatLng(34.6937, 135.5023),      // 大阪府庁
            "福岡" to LatLng(33.5890, 130.4020)       // 福岡市役所
        )

        // 大阪をデフォルト位置に
        defaultPosition = locations["大阪"]!!
    }
    else {
        // 地名と緯度経度の対応付け
        locations = mapOf(
            "現在地" to LatLng(latitude!!, longitude!!),                //現在地を追加
            "札幌" to LatLng(43.061944, 141.348889),  // 札幌市役所
            "東京" to LatLng(35.689501, 139.691722),  // 東京都庁
            "名古屋" to LatLng(35.180202, 136.906144),  // 名古屋県庁
            "大阪" to LatLng(34.6937, 135.5023),      // 大阪府庁
            "福岡" to LatLng(33.5890, 130.4020)       // 福岡市役所
        )

        // 現在地をデフォルト位置に
        defaultPosition = locations["現在地"]!!
    }


    // 現在地が存在したらdefaultPositionを現在地に変更
//    currentLocation?.let {
//        defaultPosition = LatLng(it.latitude, it.longitude)
//        // 現在地に青いマーカーを追加
////        markers.add(
////            MarkerOptions()
////                .position(defaultPosition)
////                .title("現在地")
////                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
////        )
//    }


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

    // クリックされたマーカーのインデックスを保持するための状態変数
    var clickedMarkerIndex by remember { mutableStateOf(-1) }

    // マーカーのクリック回数を管理するための状態
    val markerClickCounts = remember { mutableStateMapOf<LatLng, Int>() }

    // コメント部分のタップ回数を管理するための状態
    val commentClickCounts = remember { mutableIntStateOf(0) }

    // ボトムシートの状態を管理
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    // ボトムシートの表示状態を管理
    var chatflg by remember { mutableStateOf(false )}

    // マップがクリックされたかどうかを管理する状態
    var mapClicked by remember { mutableStateOf(false) }

//    var clickedPosition by remember { mutableStateOf<LatLng?>(null) }

    // 前回クリックされたインデックスを記録する状態
    var lastClickedIndex by remember { mutableStateOf<Int?>(null) }

    // 画面全体を埋めるBoxコンポーネント
    Box(Modifier.fillMaxSize()) {
        // Google Mapを表示
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true), //現在地
            onMapClick={latLng ->
                // マップがクリックされたかの判定を取得
                mapClicked=true
                lastClickedIndex=null
            }

//            locationSource = locationSource
        ) {

            // 読み込んだマーカー情報をマップに追加
            markers.forEachIndexed  { index, markerOptions ->
                // マーカーが何回クリックされたかを取得するための変数
                val position = markerOptions.position   //マーカーの位置を取得
                var clickCount = markerClickCounts[position] ?: 0   //　マップからクリック回数を取得し、存在しない場合は0を返す
                // InfoWindowが表示されているかどうかを管理する状態
                var infoWindowVisible by remember(index) { mutableStateOf(false) }

                var MarkerClickCount by remember(index) { mutableStateOf(0) }
                var MarkerClicked by remember(index) { mutableStateOf(false) }



                Marker(
                    state = rememberMarkerState(position = position),
                    title = markerOptions.title,
                    snippet = markerOptions.snippet,
                    icon = markerOptions.icon,
//                    icon = BitmapDescriptorFactory.defaultMarker(color_enemy),
                    // マーカークリック
//                    onClick = {
//                        // クリックされたマーカーの位置を取得
//                        val markerPosition = markerOptions.position
//
//                        // オフセットを設定（例：緯度を0.008度上にズラす）
//                        val offset = 0.008
//                        val newPosition = LatLng(markerPosition.latitude + offset, markerPosition.longitude)
//
//                        // カメラを新しい位置に移動
//                        coroutineScope.launch {
//                            cameraPositionState.animate(
//                                CameraUpdateFactory.newLatLngZoom(newPosition, defaultZoom),
//                                750 // アニメーション時間（ミリ秒）
//                            )
//                        }
//                        false // マーカーのデフォルトの動作を無効にする
//                    },

                    onClick = {

                        // クリックされたマーカーの位置を取得
                        val markerPosition = markerOptions.position

                        // オフセットを設定（例：緯度を0.008度上にズラす）
                        val offset = 0.008
                        val newPosition = LatLng(markerPosition.latitude + offset, markerPosition.longitude)

                        // カメラを新しい位置に移動
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(newPosition, defaultZoom),
                                750 // アニメーション時間（ミリ秒）
                            )
                        }

                        println("Marker at $position clicked for the second time!")

                        println("$MarkerClickCount $index")
                        println("$lastClickedIndex $index")


                        if(mapClicked==true)
                        {
                            MarkerClickCount=0
                            mapClicked=false
                            lastClickedIndex=index
//                            MarkerClicked=true
                        }
                        else
                        {
                            if(lastClickedIndex==index)
                            {
                                MarkerClickCount = 1
                            }
                            else
                            {
                                MarkerClickCount=0
                                lastClickedIndex=index
                            }
                        }

                        MarkerClickCount+=1


                        if(MarkerClickCount==2)
                        {
                            // ここにinfo windowクリック時の動作を追加
                            clickedMarkerIndex = index

                            // ボトムシートを表示
                            coroutineScope.launch()
                            {
                                bottomSheetState.show()
                                Log.d("BottomSheet", "BottomSheet shown")
                                chatflg = true
//                                MarkerClickCount = 1
                                lastClickedIndex=index
                            }
                        }


                        false

                    },

                    // ウィンドウクリック
                    onInfoWindowClick = {
                        // ここにinfo windowクリック時の動作を追加
                        clickedMarkerIndex = index

                        // ボトムシートを表示
                        coroutineScope.launch {
                            bottomSheetState.show()
                            Log.d("BottomSheet", "BottomSheet shown")
                            chatflg = true

                            MarkerClickCount = 0
                            infoWindowVisible=true
                        }
                    }
                )
                {
                    if(mapClicked==true)
                    {
                        MarkerClicked=false
                    }
                }
            }
        }


        // クリックされたマーカーのチャットを表示する
        if (chatflg && clickedMarkerIndex != -1) {

            // ボトムシートで表示
            ModalBottomSheet(
                onDismissRequest = {
                    chatflg = false
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                },
                sheetState = bottomSheetState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ){
                // チャット画面を開く
                if (clickedMarkerIndex in imageViewModel.pictureName.indices) {
                    Log.i("MapMarkers", "clickedMarkerIndex is Ok: $clickedMarkerIndex")
                    // clickedMarkerIndexが範囲内の場合のみアクセス
                    Posts(
                        imageViewModel.pictureName[clickedMarkerIndex].bitmap,
                        imageViewModel.pictureName[clickedMarkerIndex].name,
                        imageViewModel.pictureName[clickedMarkerIndex].title,
                        imageViewModel.pictureName[clickedMarkerIndex].location,
                        imageViewModel.pictureName[clickedMarkerIndex].discoveryDate,
                        imageViewModel.pictureName[clickedMarkerIndex].latitude,
                        imageViewModel.pictureName[clickedMarkerIndex].longitude,
                        imageViewModel.pictureName[clickedMarkerIndex].id,
                    )
                } else {
                    Log.e("MapMarkers", "clickedMarkerIndex is out of bounds: $clickedMarkerIndex")
                }
            }
        }


        // ドロップダウンメニューの状態を管理
        var expanded by remember { mutableStateOf(false) }
        val options = locations.keys.toList()
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        //
        if(mapL == true) {
// 画面右上に配置するBoxコンポーネント
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 14.dp) // 上の余白を増やす
            ) {

                Box(
                    modifier = Modifier
                        .size(45.dp) // アイコンのサイズに合わせて調整
                        .clip(CircleShape) // 円形にクリップ
                        .background(color = androidx.compose.ui.graphics.Color.White) // 白い背景
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
        else{
            // 画面右上に配置するBoxコンポーネント
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 14.dp) // 上の余白を増やす
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp) // アイコンのサイズに合わせて調整
                        .clip(CircleShape) // 円形にクリップ
                        .background(color = androidx.compose.ui.graphics.Color.White) // 白い背景
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


    }

    // マーカーの数をログに出力
    println("危険マーカーの数: ${markers.size}")
    Log.i("GoogleMap", "GoogleMap_Mark_End")
}


// 画像を正方形に切り抜き、リサイズし、外枠を追加
@Composable
fun ResizeMarkerIcon(
    bitmap: Bitmap,     // 元のビットマップ画像
    targetSize: Int,        // 最終的な画像のサイズ（ピクセル単位）
    borderSize: Int,        // ボーダーのサイズ（ピクセル単位）
    backgroundColor: String,        // 背景色（例: "#FDEFF2"）
    triangleColor: String       // 三角形の色（例: "#89C3EB"）
): Bitmap {
    val triangleHeight = 40 // 三角形の高さを設定
    val triangleWidth = 50 // 三角形の幅を設定
    val triangleOffset = 0 // 三角形の位置を設定

    // 画像の幅と高さの最小値を取得し、正方形のサイズを決定
    val dimension = Math.min(bitmap.width, bitmap.height)
    // 画像の中心を計算
    val x = (bitmap.width - dimension) / 2
    val y = (bitmap.height - dimension) / 2

    // 画像を正方形に切り抜く
    val squareBitmap = Bitmap.createBitmap(bitmap, x, y, dimension, dimension)
    // 正方形の画像をリサイズして、ボーダーサイズを考慮したターゲットサイズに調整
    val resizedBitmap = Bitmap.createScaledBitmap(squareBitmap, targetSize - 2 * borderSize, targetSize - 2 * borderSize, true)

    // 最終的なビットマップを作成（ターゲットサイズと三角形の高さを含む）
    val finalBitmap = Bitmap.createBitmap(targetSize, targetSize + triangleHeight + triangleOffset, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(finalBitmap)
    // 背景色を設定
    canvas.drawColor(Color.parseColor(backgroundColor))
    // リサイズした画像をキャンバスに描画
    canvas.drawBitmap(resizedBitmap, borderSize.toFloat(), borderSize.toFloat(), null)

    // 逆三角形の底辺を描画するためのペイントオブジェクトを作成
    val paint = Paint()
    paint.color = Color.parseColor(triangleColor) // 三角形の色を設定
    paint.style = Paint.Style.FILL

    // 逆三角形のパスを作成
    val path = android.graphics.Path()
    path.moveTo((targetSize / 2).toFloat(), (targetSize + triangleHeight).toFloat()) // 三角形の頂点
    path.lineTo((targetSize / 2 - triangleWidth / 2).toFloat(), targetSize.toFloat()) // 三角形の左下
    path.lineTo((targetSize / 2 + triangleWidth / 2).toFloat(), targetSize.toFloat()) // 三角形の右下
    path.close()

    // キャンバスに三角形を描画
    canvas.drawPath(path, paint)

    // 下部の白い背景を削除するためにビットマップを切り抜く
    val croppedBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, targetSize, targetSize + triangleHeight + triangleOffset)

    // 下部の背景を透明に設定
    for (i in 0 until croppedBitmap.width) {
        for (j in targetSize until croppedBitmap.height) {
            if (croppedBitmap.getPixel(i, j) == Color.parseColor(backgroundColor)) {
                croppedBitmap.setPixel(i, j, Color.TRANSPARENT)
            }
        }
    }

    // 最終的なビットマップを返す
    return croppedBitmap
}
