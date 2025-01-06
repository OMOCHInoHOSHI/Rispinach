import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.DataType

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
        return try {
            // 画像の解析の開始を確認
            Log.d("ImageAnalyzer", "analyzePhoto_Start")

            // モデルの入力サイズに合わせて画像をリサイズ
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)
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

// 画像選択画面の設定(ResNet)---------------------------------------------------------------
@Composable
fun ResNetPage() {
    // 関数の開始を確認
    Log.d("ResNet_page", "ResNet_Start")

    // 現在のコンテキストを取得
    val context = LocalContext.current
    // ImageAnalyzerのインスタンスを作成
    val imageAnalyzer = remember { ImageAnalyzer(context) }
    // 選択された画像を保持するための状態を定義
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    // 解析結果を保持するための状態を定義
    var result by remember { mutableStateOf("") }
    // コルーチンスコープを作成
    val coroutineScope = rememberCoroutineScope()

    // 画像選択のランチャーを作成
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // 選択された画像のURIからビットマップを取得
            val inputStream = context.contentResolver.openInputStream(it)
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    // マテリアルテーマを適用
    MaterialTheme {
        // 画面全体を覆うSurfaceを作成
        Surface(modifier = Modifier.fillMaxSize()) {
            // 縦に並べるColumnを作成
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ビットマップが存在する場合、画像を表示
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(224.dp)
                    )
                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 画像選択ボタンを作成
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("写真を選択")
                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 画像解析ボタンを作成
                Button(onClick = {
                    bitmap?.let { bmp ->
                        // コルーチンを起動して画像を解析
                        coroutineScope.launch {
                            try {
                                Log.d("ResNet_page", "imageAnalyzer")
                                result = imageAnalyzer.analyzePhoto(bmp)
                            } catch (e: Exception) {
                                Log.e("ResNet_page", "Error analyzing photo", e)
                            }
                        }
                    }
                }) {
                    Text("解析")
                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 解析結果を表示
                Text(text = result)
            }
        }
    }
}

// 画像選択画面の設定(ResNet)---------------------------------------------------------------
@Composable
fun ResNetPage(bitmaps: Bitmap) {
    // 関数の開始を確認
    Log.d("ResNet_page", "ResNet_Start")

    // 現在のコンテキストを取得
    val context = LocalContext.current
    // ImageAnalyzerのインスタンスを作成
    val imageAnalyzer = remember { ImageAnalyzer(context) }
    // 選択された画像を保持するための状態を定義
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    // 解析結果を保持するための状態を定義
    var result by remember { mutableStateOf("") }
    // コルーチンスコープを作成
    val coroutineScope = rememberCoroutineScope()

    // 画像選択のランチャーを作成
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            // 選択された画像のURIからビットマップを取得
//            val inputStream = context.contentResolver.openInputStream(it)
//            bitmap = BitmapFactory.decodeStream(inputStream)
//        }
//    }

    bitmap = bitmaps

    // マテリアルテーマを適用
    MaterialTheme {
        // 画面全体を覆うSurfaceを作成
        Surface(modifier = Modifier.fillMaxSize()) {
            // 縦に並べるColumnを作成
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ビットマップが存在する場合、画像を表示
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(224.dp)
                    )
                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 画像選択ボタンを作成
//                Button(onClick = { launcher.launch("image/*") }) {
//                    Text("写真を選択")
//                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 画像解析ボタンを作成
                Button(onClick = {
                    bitmap?.let { bmp ->
                        // コルーチンを起動して画像を解析
                        coroutineScope.launch {
                            try {
                                Log.d("ResNet_page", "imageAnalyzer")
                                result = imageAnalyzer.analyzePhoto(bmp)
                            } catch (e: Exception) {
                                Log.e("ResNet_page", "Error analyzing photo", e)
                            }
                        }
                    }
                }) {
                    Text("解析")
                }
                // スペースを追加
                Spacer(modifier = Modifier.height(16.dp))
                // 解析結果を表示
                Text(text = result)
            }
        }
    }
}