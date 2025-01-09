package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(bitmap: Bitmap?) {
    // 投稿タイトルの状態を保持する変数
    var title by rememberSaveable { mutableStateOf("") }
    // 生物名の状態を保持する変数
    var speciesName by rememberSaveable { mutableStateOf("") }
    // 発見場所の状態を保持する変数
    var location by rememberSaveable { mutableStateOf("") }
    // 発見日付の状態を保持する変数
    var discoveryDate by rememberSaveable { mutableStateOf("") }
    // 生物名が入力されたかどうかを保持する変数
    var speciesNameSet by remember { mutableStateOf(false) }
    // コルーチンスコープを作成
    val coroutineScope = rememberCoroutineScope()

    // 現在のコンテキストを取得
    val context = LocalContext.current
    // ImageAnalyzerのインスタンスを作成
    val imageAnalyzer = remember { ImageAnalyzer(context) }

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
                onClick = { /* TODO: 投稿機能を実装 */ },
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
            OutlinedTextField(
                value = speciesName,
                onValueChange = { speciesName = it },
                placeholder = { Text("生物名入力") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable() {
                        Log.d("PostScreen_image", "Clickable triggered")
                        if (!speciesNameSet) {
                            bitmap?.let { bmp ->
                                coroutineScope.launch {
                                    speciesName = imageAnalyzer.analyzePhoto(bmp)
                                    Log.d("PostScreen_image", "speciesName before setting: $speciesName")
                                    speciesNameSet = true
                                }
                            }
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

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
            OutlinedTextField(
                value = discoveryDate,
                onValueChange = { discoveryDate = it },
                placeholder = { Text("発見日付") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
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
            if (maxIndex != -1) labels[maxIndex] else "Unknown"

        } catch (e: Exception) {        // 画像の解析に失敗した場合(エラーログ)
            Log.e("ImageAnalyzer", "Error analyzing photo", e)
            "Error analyzing photo"
        }
    }
}