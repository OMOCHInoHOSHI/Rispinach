package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import ResNetPage
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.media.ExifInterface
import androidx.compose.ui.platform.LocalContext

//@Composable
//fun photosPick(onNothingSelected: () -> Unit,): Uri?{
//
//    //初期値に空のURI
//    var pickedImageUri by remember { mutableStateOf(Uri.EMPTY) }
//
//    val launcher = rememberLauncherForActivityResult(
//        //像選択のインテントを起動するための ActivityResultLauncher を作成
//        ActivityResultContracts.PickVisualMedia()
//    ){ uri: Uri? ->    //Uri?はヌル許容型
//        uri?.let {
//            //pickedImageUriに選択された画像のuriを代入
//            pickedImageUri = it
//            Log.d("MainActivity", "image selected")
//            println("URIゲット")
//        } ?: onNothingSelected()    //uriがnull
//    }//ライフサイクル   //Composeが最初に組み込まれたときに実行
//    LaunchedEffect(true) {
//        //1つの画像を選択
//        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }
//
//    return pickedImageUri
//}

@Composable
fun photosPick2(onNothingSelected: () -> Unit,){

    //初期値に空のURI
    var pickedImageUri by remember { mutableStateOf(Uri.EMPTY) }

    val launcher = rememberLauncherForActivityResult(
        //像選択のインテントを起動するための ActivityResultLauncher を作成
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->    //Uri?はヌル許容型
        uri?.let {
            //pickedImageUriに選択された画像のuriを代入
            pickedImageUri = it
            Log.d("MainActivity", "image selected")
            println("URIゲット")
        } ?: onNothingSelected()    //uriがnull
    }//ライフサイクル   //Composeが最初に組み込まれたときに実行
    LaunchedEffect(true) {
        //1つの画像を選択
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Uriを関数に渡す
    if(pickedImageUri != Uri.EMPTY){
        val bitmap = image_Uri_to_Bitmap(pickedImageUri)

        // bitmapで判定
        if(bitmap is Bitmap){
            val cameraState = remenbreCameraState()
            //カメラ停止
            cameraState.stopCamera()
            println("bitmap取得")
            // 画像の回転情報を考慮して正しい向きに回転させる
            val rotatedBitmap = rotateBitmapIfRequired(LocalContext.current, bitmap, pickedImageUri)
            PostScreen(rotatedBitmap)
            // bitmap判定
            //ResNetPage(rotatedBitmap)
        }

    }

}

// 画像の回転が必要かどうかを確認し、必要であれば回転を適用する
fun rotateBitmapIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
    // 画像のURIから入力ストリームを開く
    val inputStream = context.contentResolver.openInputStream(uri)
    // 入力ストリームからEXIF情報を取得
    val exif = inputStream?.let { ExifInterface(it) }
    // EXIF情報から画像の回転方向を取得（デフォルトは正常な向き）
    val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // 画像の回転方向に応じて適切な回転を適用
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)  // 90度回転
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180) // 180度回転
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270) // 270度回転
        else -> bitmap  // 回転不要の場合はそのまま返す
    }
}

// 指定された角度でビットマップを回転させる
fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    // 回転行列を作成し、指定された角度で回転を適用
    val matrix = android.graphics.Matrix().apply { postRotate(degrees.toFloat()) }
    // 回転行列を使用して新しいビットマップを作成し、回転を適用
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}