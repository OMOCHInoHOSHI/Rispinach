package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

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

@Composable
fun photosPick(onNothingSelected: () -> Unit,): Uri?{

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

    return pickedImageUri
}

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
        image_Uri(pickedImageUri)
    }

}