package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
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

    // Firebaseの初期化
    FirebaseApp.initializeApp(context)
    Log.d("TransmitData", "Firebase initialized")

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
//                    TransmitData(bitmap, title.ifEmpty { "無題" }, speciesName.ifEmpty { "不明" }, location.ifEmpty { "不明" }, discoveryDate.ifEmpty { "不明" }) },
                    TransmitData(
                        bitmap,
                        title.ifEmpty { "無題" },
                        speciesName.ifEmpty { "不明" },
                        location.ifEmpty { "不明" },
                        discoveryDate.ifEmpty { "不明" },
                        context
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("投稿タイトル入力") },
                modifier = Modifier.fillMaxWidth(),
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
                )
            )

            // 生物名入力フィールドの設定
            Box {
                OutlinedTextField(
                    value = speciesName, // 入力された生物名の状態を保持
                    onValueChange = { speciesName = it }, // 生物名が変更されたときの処理
                    placeholder = { Text("生物名入力") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color(0xFF89C3EB), // アイコンを勿忘草色(わすれなぐさいろ)に変更
                            modifier = Modifier.clickable {
                                Log.d("PostScreen_image", "Icon clicked") // クリック時のログ

                                bitmap?.let { bmp ->
                                    coroutineScope.launch {
                                        val result = imageAnalyzer.analyzePhoto(bmp) // 画像解析

                                        speciesName = result // 解析結果を生物名に設定
                                        speciesNameSet = true // 生物名が設定されたことを記録
                                        Log.d("PostScreen_image", "speciesName set to: $speciesName") // 解析結果のログ
                                    }
                                }
                            }
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }

            // 発見場所入力フィールドの設定
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("発見場所") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // 発見日付入力フィールドの設定
//            OutlinedTextField(
//                value = discoveryDate,
//                onValueChange = { discoveryDate = it },
//                placeholder = { Text("発見日付") },
//                modifier = Modifier.fillMaxWidth()
//                    .clickable { date_flg = true },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.DateRange,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.outline
//                    )
//                },
//
//                shape = RoundedCornerShape(8.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
//                )
//            )
            // 発見日付入力フィールドの設定
            SelectOutlineTextField(
                value = discoveryDate,
                onValueChange = { discoveryDate = it },
                onClick = { date_flg = true },
            )


            // 日付カレンダーを表示-------------------------------------------------------
            val focusManager = LocalFocusManager.current
            if(date_flg){
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

// 投稿データの送信
fun TransmitData(bitmap: Bitmap?, title: String, speciesName: String, location: String, discoveryDate: String) {
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
        .setCustomMetadata("title", title.ifEmpty { "無題" }) // タイトルが空の場合は「無題」とする
        .setCustomMetadata("speciesName", speciesName.ifEmpty { "不明" }) // 生物名が空の場合は「不明」とする
        .setCustomMetadata("location", location.ifEmpty { "不明" }) // 発見場所が空の場合は「不明」とする
        .setCustomMetadata("discoveryDate", discoveryDate.ifEmpty { "不明" }) // 発見日付が空の場合は「不明」とする
        .build()
    Log.d("TransmitData", "metadata")

    // Firebase Storage にアップロード
    val uploadTask = imagesRef.putBytes(data, metadata)
    uploadTask.addOnFailureListener { exception ->
        Log.e("TransmitData", "Upload failed", exception)
    }.addOnSuccessListener { taskSnapshot ->
        Log.d("TransmitData", "Upload successful")
    }
}

// 投稿データの送信
fun TransmitData(
    bitmap: Bitmap?,
    title: String,
    speciesName: String,
    location: String,
    discoveryDate: String,
    context: Context
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
        .setCustomMetadata("title", title.ifEmpty { "無題" }) // タイトルが空の場合は「無題」とする
        .setCustomMetadata("speciesName", speciesName.ifEmpty { "不明" }) // 生物名が空の場合は「不明」とする
        .setCustomMetadata("location", location.ifEmpty { "不明" }) // 発見場所が空の場合は「不明」とする
        .setCustomMetadata("discoveryDate", discoveryDate.ifEmpty { "不明" }) // 発見日付が空の場合は「不明」とする
        .build()
    Log.d("TransmitData", "metadata")

    // アップロード中のフラグ
    var isUploading = false
    // アップロード開始
    isUploading = true
    Toast.makeText(context, "アップロード中です...", Toast.LENGTH_SHORT).show()

    // Firebase Storage にアップロード
    val uploadTask = imagesRef.putBytes(data, metadata)
    uploadTask.addOnFailureListener { exception ->
        Log.e("TransmitData", "Upload failed", exception)
        // アップロード失敗時にToastメッセージを表示
        Toast.makeText(context, "アップロードに失敗しました: ${exception.message}", Toast.LENGTH_SHORT).show()
        isUploading = false
    }.addOnSuccessListener { taskSnapshot ->
        Log.d("TransmitData", "Upload successful")
        // アップロード成功時にToastメッセージを表示
        Toast.makeText(context, "アップロードが成功しました！", Toast.LENGTH_SHORT).show()
        isUploading = false
    }
}