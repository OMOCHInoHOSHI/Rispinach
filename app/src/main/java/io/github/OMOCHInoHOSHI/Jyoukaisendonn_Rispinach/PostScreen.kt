package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import java.util.Locale
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.material.icons.filled.Clear
import com.google.maps.android.compose.MarkerState


// 投稿が成功したかを確認し、Homeの更新が必要かを管理するViewModelS----------------
class Post_SucsessViewModel : ViewModel() {

    // アップロードが成功したか確認
    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> get() = _uploadSuccess

    // コールバック関数
    private var onUploadStatusChanged: ((Boolean) -> Unit)? = null

    // 投稿が成功したかを確認する関数
    fun setUploadSuccess(success: Boolean) {
        _uploadSuccess.value = success
        // 状態を変更する際にコールバックを実行
        onUploadStatusChanged?.invoke(success)
    }

    // コールバックを登録する
    fun setOnUploadStatusChangedListener(callback: (Boolean) -> Unit) {
        onUploadStatusChanged = callback
    }

    // 現在の uploadSuccess の状態を取得
    fun checkUploadSuccess(): Boolean {
        return _uploadSuccess.value
    }
}
// 投稿が成功したかを確認し、Homeの更新が必要かを管理するViewModelS----------------


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(bitmap: Bitmap?, cameraViewModel: CameraViewModel = viewModel()) {
    // 投稿タイトルの状態を保持する変数
    var title by rememberSaveable { mutableStateOf("") }
    // 生物名の状態を保持する変数
    var speciesName by rememberSaveable { mutableStateOf("") }
    // 発見場所の状態を保持する変数
    var location by rememberSaveable { mutableStateOf("") }
    // 発見日付の状態を保持する変数
    var discoveryDate by rememberSaveable { mutableStateOf("") }
    // 日付が押されたかのフラグ
    var date_flg by rememberSaveable { mutableStateOf(false) }
    // 生物名が入力されたかどうかを保持する変数
    var speciesNameSet by remember { mutableStateOf(false) }
    // コルーチンスコープを作成
    val coroutineScope = rememberCoroutineScope()

    // 現在のコンテキストを取得
    val context = LocalContext.current
    // ImageAnalyzerのインスタンスを作成
    val imageAnalyzer = remember { ImageAnalyzer(context) }

    // ViewModelのインスタンスを取得
    val viewModel = viewModel<Post_SucsessViewModel>()

    // Firebaseの初期化
    FirebaseApp.initializeApp(context)
    Log.d("TransmitData", "Firebase initialized")

    // リアルタイムデータベースのキーを取得
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
//    val r_t_d_key = getNewKeyFromRealtimeDatabase(myRef)
//    println("key = $r_t_d_key")

    // 表示する前にビットマップをリサイズ
    //val resizedBitmap = bitmap?.let { resizeBitmap(it, 224, 224) }

    // 発見場所の地図表示フラグ
    var showMap by remember { mutableStateOf(false) }

    // マーカーの住所を保持する状態を追加
    var markerAddress by remember { mutableStateOf("") }

    // 緯度と経度を保持する状態を追加
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    Scaffold(
        topBar = {
            // トップバーの設定
            TopAppBar(
                title = { Text("投稿準備画面") }
            )
        },
        floatingActionButton = {
            // 投稿ボタンの設定
            FloatingActionButton(
                onClick = {
                    // カメラの表示状態を非表示に変更
                    cameraViewModel.setShowCamera(false)
                    // データ送信
                    TransmitData(
                        bitmap,
                        title.ifEmpty { "無題" },
                        speciesName.ifEmpty { "不明" },
                        location.ifEmpty { "不明" },
                        discoveryDate.ifEmpty { "不明" },
                        context,
//                        r_t_d_key,
                        viewModel,//コールバック用
                        latitude, // 緯度を渡す
                        longitude // 経度を渡す
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Post")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 画像エリアの設定
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Text(
                        text = "picture",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // タイトル入力フィールドの設定
            Box {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("  投稿タイトル入力") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val iconWidth = 48.dp.toPx() // アイコンの幅を考慮
                            val padding = 8.dp.toPx() // アイコンと線の間のパディング
                            val x = iconWidth + padding + strokeWidth / 2
                            drawLine(
                                color = Color.Gray,
                                start = Offset(x - 11, 0f + 22),
                                end = Offset(x - 11, size.height - 22),
                                strokeWidth = strokeWidth
                            )
                        },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    textStyle = LocalTextStyle.current.copy(textIndent = TextIndent(firstLine = 6.sp)) // テキストのインデントを設定
                )
            }

            // 生物名入力フィールドの設定
            Box {
                OutlinedTextField(
                    value = speciesName, // 入力された生物名の状態を保持
                    onValueChange = { speciesName = it }, // 生物名が変更されたときの処理
                    placeholder = { Text("  生物名入力") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val iconWidth = 48.dp.toPx() // アイコンの幅を考慮
                            val padding = 8.dp.toPx() // アイコンと線の間のパディング
                            val x = iconWidth + padding + strokeWidth / 2
                            drawLine(
                                color = Color.Gray,
                                start = Offset(x - 11, 0f + 22),
                                end = Offset(x - 11, size.height - 22),
                                strokeWidth = strokeWidth
                            )
                        },
                    leadingIcon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp) // アイコンと文字を含むボックスのサイズ
                                .background(
                                    color = androidx.compose.ui.graphics.Color(0xFF89c3eb).copy(alpha = 0.2f),      // 背景を勿忘草色(わすれなぐさいろ)に変更
                                    shape = CircleShape
                                )
                                .clickable {
                                    Log.d("PostScreen_image", "Circle clicked") // クリック時のログ

                                    bitmap?.let { bmp ->
                                        coroutineScope.launch {
                                            val result = imageAnalyzer.analyzePhoto(bmp) // 画像解析

                                            speciesName = result // 解析結果を生物名に設定
                                            speciesNameSet = true // 生物名が設定されたことを記録
                                            Log.d("PostScreen_image", "speciesName set to: $speciesName") // 解析結果のログ
                                        }
                                    }
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = androidx.compose.ui.graphics.Color(0xFF007BBB) // アイコンを紺碧色 (こんぺきいろ)に変更
                                )
                                Text(
                                    text = "AI",
                                    fontSize = 14.sp,
                                    color = androidx.compose.ui.graphics.Color(0xFF007BBB), // 文字を紺碧色 (こんぺきいろ)に変更
                                    modifier = Modifier.padding(top = 0.dp) // 文字の位置を調整
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    textStyle = LocalTextStyle.current.copy(textIndent = TextIndent(firstLine = 6.sp)) // テキストのインデントを設定
                )
            }

            // 発見場所入力フィールドの設定
            Box {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("  発見場所") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val iconWidth = 48.dp.toPx() // アイコンの幅を考慮
                            val padding = 8.dp.toPx() // アイコンと線の間のパディング
                            val x = iconWidth + padding + strokeWidth / 2
                            drawLine(
                                color = Color.Gray,
                                start = Offset(x - 11, 0f + 22),
                                end = Offset(x - 11, size.height - 22),
                                strokeWidth = strokeWidth
                            )
                        },
                    leadingIcon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp) // アイコンと文字を含むボックスのサイズ
                                .background(
                                    color = androidx.compose.ui.graphics.Color(0xFF89C3EB).copy(alpha = 0.2f),      //背景を勿忘草色(わすれなぐさいろ)に変更
                                    shape = CircleShape
                                )
                                .clickable {
                                    showMap = true
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = androidx.compose.ui.graphics.Color(0xFF007BBB) // アイコンを紺碧色 (こんぺきいろ)に変更
                                )
                                Text(
                                    text = "Map",
                                    fontSize = 14.sp,
                                    color = androidx.compose.ui.graphics.Color(0xFF007BBB), // 文字を紺碧色 (こんぺきいろ)に変更
                                    modifier = Modifier.padding(top = 0.dp) // 文字の位置を調整
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    textStyle = LocalTextStyle.current.copy(textIndent = TextIndent(firstLine = 6.sp)) // テキストのインデントを設定
                )
            }

            // 地図アイコンが押された時の処理
            if (showMap) {
//                Spacer(modifier = Modifier.height(4.dp))
                // 地図表示
                Dialog(
                    onDismissRequest = { var showPopup = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false) // 幅を制限しない
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LocatePosition(
                            onAddressChanged = { address ->
                                markerAddress = address
                            },
                            onCloseMap = { lat, lng ->
                                location = markerAddress
                                latitude = lat
                                longitude = lng
                                showMap = false
                            }
                        )
                    }
                }
            }

            // 地図アイコンが押された時の処理
            if (showMap) {
//                Spacer(modifier = Modifier.height(4.dp))
                // 地図表示
                Dialog(
                    onDismissRequest = { var showPopup = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false) // 幅を制限しない
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
//                            .fillMaxWidth()
//                            .height(300.dp) // 地図の高さを指定
                    ) {
                        LocatePosition(
                            onAddressChanged = { address ->
                                markerAddress = address
                            },
                            onCloseMap = { lat, lng ->
                                location = markerAddress
                                latitude = lat
                                longitude = lng
                                showMap = false
                            }
                        )
                    }
                }
                // 地図の後に隙間を追加
//                Spacer(modifier = Modifier.height(10.dp))

            }

            // 発見日付入力フィールドの設定
            SelectOutlineTextField(
                value = discoveryDate,
                onValueChange = { discoveryDate = it },
                onClick = { date_flg = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val iconWidth = 48.dp.toPx() // アイコンの幅を考慮
                        val padding = 8.dp.toPx() // アイコンと線の間のパディング
                        val x = iconWidth + padding + strokeWidth / 2
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x - 11, 0f + 22),
                            end = Offset(x - 11, size.height - 22),
                            strokeWidth = strokeWidth
                        )
                    }
            )
            Spacer(modifier = Modifier.height(100.dp))

            // 日付カレンダーを表示-------------------------------------------------------
            val focusManager = LocalFocusManager.current
            if (date_flg) {
                DatePickerModal(
                    // 選択時の処理
                    onDateSelected = {
                        discoveryDate = convertMillisToDate(it)
                        date_flg = false
                        focusManager.clearFocus()
                    },
                    // 未選択時の処理
                    onDismiss = {
                        date_flg = false
                        focusManager.clearFocus()
                    },
                )
            }
            // 日付カレンダーを表示E-------------------------------------------------------
        }
    }
}

class ImageAnalyzer(context: Context) {
    // TensorFlow Liteのインタープリター（モデルを実行するためのクラス）
    private val interpreter: Interpreter

    // クラス名（生物の名称）を保持するリスト
    private val labels: List<String>

    init {
        // モデルファイルをロード
        interpreter = try {
            val modelFile = loadModelFile(context, "resnet50_model.tflite")
            Log.i("ImageAnalyzer", "モデルをロードできました")
            Interpreter(modelFile)
        } catch (e: Exception) {
            Log.e("ImageAnalyzer", "モデルをロードできませんでした", e)
            throw RuntimeException("モデルをロードできませんでした", e)
        }

        // クラス名ファイルをロード(生物の名称)
        labels = try {
            loadLabels(context, "classes.txt")
        } catch (e: Exception) {
            Log.e("ImageAnalyzer", "クラス名ファイルをロードできませんでした", e)
            throw RuntimeException("クラス名ファイルをロードできませんでした", e)
        }
        Log.i("ImageAnalyzer", "クラス名ファイルのロード処理終了")
    }

    // モデルファイルをロードする関数
    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).also {
            inputStream.close()
            fileChannel.close()
        }
    }

    // クラス名ファイルをロードする関数
    private fun loadLabels(context: Context, fileName: String): List<String> {
        return context.assets.open(fileName).bufferedReader().useLines { it.toList() }
    }

    // 画像を解析する関数
    fun analyzePhoto(bitmap: Bitmap): String {
        Log.d("ImageAnalyzer", "analyzePhoto called")

        return try {
            // 画像の解析の開始を確認
            Log.d("ImageAnalyzer", "analyzePhoto_Start")


            // モデルの入力サイズ
            val targetWidth = 224
            val targetHeight = 224

            // 画像の縦横比を維持しながらリサイズ
            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val resizedBitmap = if (bitmap.width > bitmap.height) {
                Bitmap.createScaledBitmap(bitmap, targetWidth, (targetWidth / aspectRatio).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(bitmap, (targetHeight * aspectRatio).toInt(), targetHeight, true)
            }

            // 余白を追加して224x224にする
            val finalBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(finalBitmap)
            val left = (targetWidth - resizedBitmap.width) / 2
            val top = (targetHeight - resizedBitmap.height) / 2
            canvas.drawBitmap(resizedBitmap, left.toFloat(), top.toFloat(), null)



            Log.d("ImageAnalyzer", "analyzePhoto_1")

            // リソースの解放
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(finalBitmap)
            //tensorImage.load(bitmap)
            Log.d("ImageAnalyzer", "analyzePhoto_1")

            // バッファの形式を確認
            val byteBuffer = tensorImage.tensorBuffer.buffer
            Log.d("ImageAnalyzer", "TensorImage buffer: $byteBuffer")
            Log.d("ImageAnalyzer", "Buffer capacity: ${byteBuffer.capacity()}")

            // 確認のためにバッファのサイズを出力
            val inputShape = interpreter.getInputTensor(0).shape()
            val inputSize = inputShape.reduce { acc, i -> acc * i }
            Log.d("ImageAnalyzer", "Expected input size: $inputSize")

            val output = Array(1) { FloatArray(labels.size) }
            Log.d("ImageAnalyzer", "analyzePhoto_2")

            // モデルを実行して結果を取得
            interpreter.run(byteBuffer, output)
            Log.d("ImageAnalyzer", "analyzePhoto_3")
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            Log.d("ImageAnalyzer", "analyzePhoto_4")
            if (maxIndex != -1) {
                val label = labels[maxIndex]
                label.substringBefore(",") // 1つ目のコンマまでの文字列を取得
            } else {
                "Unknown"
            }

        } catch (e: Exception) {        // 画像の解析に失敗した場合(エラーログ)
            Log.e("ImageAnalyzer", "Error analyzing photo", e)
            "Error analyzing photo"
        }
    }
}

// 画像のリサイズ
fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val resizedBitmap = if (bitmap.width > bitmap.height) {
        Bitmap.createScaledBitmap(bitmap, targetWidth, (targetWidth / aspectRatio).toInt(), true)
    } else {
        Bitmap.createScaledBitmap(bitmap, (targetHeight * aspectRatio).toInt(), targetHeight, true)
    }

    val finalBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(finalBitmap)
    val left = (targetWidth - resizedBitmap.width) / 2
    val top = (targetHeight - resizedBitmap.height) / 2
    canvas.drawBitmap(resizedBitmap, left.toFloat(), top.toFloat(), null)

    return finalBitmap
}

// 発見場所指定マップの表示
@Composable
fun LocatePosition(onAddressChanged: (String) -> Unit, onCloseMap: (Double?, Double?) -> Unit) {
    // 地名と緯度経度の対応付け
    val locations = mapOf(
        "札幌" to LatLng(43.061944, 141.348889),  // 札幌市役所
        "東京" to LatLng(35.689501, 139.691722),  // 東京都庁
        "大阪" to LatLng(34.6937, 135.5023),      // 大阪府庁
        "福岡" to LatLng(33.5890, 130.4020)       // 福岡市役所
    )
    // デフォルトのカメラ位置を東京都庁に設定
    val defaultPosition = locations["東京"]!!
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 13f)
    }

    // CoroutineScopeをrememberで保持
    val coroutineScope = rememberCoroutineScope()

    // マーカーの位置を保持する状態を追加
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var markerAddress by remember { mutableStateOf("") } // 住所情報も状態として保持

    // 逆ジオコーディングのためのGeocoderを取得
    val context = LocalContext.current
    val geocoder = Geocoder(context, Locale.getDefault())



    Box(Modifier.fillMaxSize()) {
        // GoogleMapコンポーザブルを表示
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // マップがクリックされたときの処理
                markerPosition = latLng

                // 逆ジオコーディングで住所を取得
                coroutineScope.launch {
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    markerAddress =
                        addresses?.firstOrNull()?.getAddressLine(0) ?: "住所が見つかりません"
                    onAddressChanged(markerAddress)
                }
            }
        ) {
            // マーカーを表示
            markerPosition?.let {position ->
                val markerState = MarkerState(position) // 新しいMarkerStateを作成
                Marker(
                    state = markerState,
                    title = "選択した場所"
                )
            }
        }

        var expanded by remember { mutableStateOf(false) }
        val options = locations.keys.toList()
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        // 右上に✕ボタンを追加
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            // ✕ボタンの表示
            IconButton(onClick = {
                onCloseMap(
                    markerPosition?.latitude,
                    markerPosition?.longitude
                )
            }) {
                Icon(Icons.Default.Close, contentDescription = "閉じる", tint = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // その他のオプションボタンの表示
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "その他のオプション")
            }
//            //クリアボタンの追加
//            Row(
//                verticalAlignment = Alignment.CenterVertically // 垂直方向中央揃え
//            ){
//                Button(
//                    onClick = {
//                        markerPosition = null // ピンをクリア
//                        markerAddress = ""      // 住所情報をクリア
//                        onAddressChanged("") // 住所情報をクリア
//                    },
//                    colors = ButtonDefaults.buttonColors( // 色を設定
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    )
//                ) {
//                    Text("クリア")
//                }
//            }
        }


        // ドロップダウンメニューの表示
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
                                    ), // ズームレベルも変更
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

//// 投稿データの送信
//fun TransmitData(bitmap: Bitmap?, title: String, speciesName: String, location: String, discoveryDate: String) {
//    if (bitmap == null) {
//        Log.e("TransmitData", "Bitmap is null")
//        return
//    }
//
//    // Firebase Storage のインスタンスを取得
//    val storage = Firebase.storage
//    val storageRef = storage.reference
//    val imagesRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
//    Log.d("TransmitData", "TransmitData_1")
//
//    // Bitmap を JPEG に変換
//    val baos = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//    val data = baos.toByteArray()
//    Log.d("TransmitData", "image")
//
//    // メタデータを作成
//    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
//        .setCustomMetadata("title", title.ifEmpty { "無題" }) // タイトルが空の場合は「無題」とする
//        .setCustomMetadata("speciesName", speciesName.ifEmpty { "不明" }) // 生物名が空の場合は「不明」とする
//        .setCustomMetadata("location", location.ifEmpty { "不明" }) // 発見場所が空の場合は「不明」とする
//        .setCustomMetadata("discoveryDate", discoveryDate.ifEmpty { "不明" }) // 発見日付が空の場合は「不明」とする
//        .build()
//    Log.d("TransmitData", "metadata")
//
//    // Firebase Storage にアップロード
//    val uploadTask = imagesRef.putBytes(data, metadata)
//    uploadTask.addOnFailureListener { exception ->
//        Log.e("TransmitData", "Upload failed", exception)
//    }.addOnSuccessListener { taskSnapshot ->
//        // 画像URLを取得
//        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { imageURL ->
//            // 投稿IDを生成（push()を使って一意なIDを生成）
//            val postsRef = Firebase.database.reference.child("posts")
//            val newPostRef = postsRef.push()  // 一意な投稿IDを生成
//            val postId = newPostRef.key  // 新しく生成されたIDを取得
//
//            // 投稿メタデータをRealtime Databaseに保存
//            val postData = mapOf(
//                "imageURL" to imageURL.toString(),
//                "title" to title,
//                "speciesName" to speciesName,
//                "location" to location,
//                "discoveryDate" to discoveryDate,
//                "timestamp" to ServerValue.TIMESTAMP
//            )
//            newPostRef.setValue(postData)
//
//            // 投稿IDを保存（コメントを投稿IDに紐づけるために使います）
//            Log.d("TransmitData", "Post uploaded with ID: $postId")
//        }
//    }
//}

// 投稿データの送信
fun TransmitData(
    bitmap: Bitmap?,
    title: String,
    speciesName: String,
    location: String,
    discoveryDate: String,
    context: Context,
//    r_t_d_Key: String,
    viewModel: Post_SucsessViewModel,
    latitude: Double?, // 緯度を追加
    longitude: Double? // 経度を追加
) {
    if (bitmap == null) {
        Log.e("TransmitData", "Bitmap is null")
        return
    }

    // Firebase Storage のインスタンスを取得
    val storage = Firebase.storage
    val storageRef = storage.reference
    val imagesRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
    Log.d("TransmitData", "TransmitData_1")

    // Bitmap を JPEG に変換
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()
    Log.d("TransmitData", "image")

    // メタデータを作成
    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
        .setCustomMetadata("title", title.ifEmpty { "無題" })
        .setCustomMetadata("speciesName", speciesName.ifEmpty { "不明" })
        .setCustomMetadata("location", location.ifEmpty { "不明" })
        .setCustomMetadata("discoveryDate", discoveryDate.ifEmpty { "不明" })
//        .setCustomMetadata("R_T_D_Key", r_t_d_Key.ifEmpty { "R_T_D_Key取得失敗" })
        .setCustomMetadata("latitude", latitude?.toString() ?: "不明") // 緯度を追加
        .setCustomMetadata("longitude", longitude?.toString() ?: "不明") // 経度を追加
        .build()
    Log.d("TransmitData", "metadata")

    // アップロード中のフラグ
    var isUploading = false
    isUploading = true
    Toast.makeText(context, "アップロード中です...", Toast.LENGTH_SHORT).show()

    // Firebase Storage にアップロード
    val uploadTask = imagesRef.putBytes(data, metadata)
    uploadTask.addOnFailureListener { exception ->
        Log.e("TransmitData", "Upload failed", exception)
        // アップロード失敗時にToastメッセージを表示
        Toast.makeText(context, "アップロードに失敗しました: ${exception.message}", Toast.LENGTH_SHORT).show()
        isUploading = false
        // アップロード失敗時にViewModelのフラグを更新
        viewModel.setUploadSuccess(false)
    }.addOnSuccessListener { taskSnapshot ->
        Log.d("TransmitData", "Upload successful")
        // アップロード成功時にToastメッセージを表示
        Toast.makeText(context, "アップロードが成功しました！", Toast.LENGTH_SHORT).show()
        isUploading = false
        // アップロード成功時にViewModelのフラグを更新
        viewModel.setUploadSuccess(true)
        println("viewModel.checkUploadSuccess() = ")
        println(viewModel.checkUploadSuccess())
    }
}