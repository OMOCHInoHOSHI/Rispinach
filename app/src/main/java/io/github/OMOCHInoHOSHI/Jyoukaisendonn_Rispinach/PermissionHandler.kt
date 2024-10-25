package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionHand(
    onGranted:(Boolean) -> Unit,    //onGrantedがTureならカメラ許可
){
    //コンポーザー内のアクティビティの結果を取得できる
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){granted ->
        onGranted(granted)
    }

    //カメラの権限が許可されてるかの確認
    val context = LocalContext.current
    if(androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        )==android.content.pm.PackageManager.PERMISSION_GRANTED
    ){
        onGranted(true) //許可されている
    }else{
        //ランチャーマニフェストパーミッションカメラを呼び出し、ユーザーにカメラの権限を要求
        SideEffect {    //UI
            launcher.launch(android.Manifest.permission.CAMERA)
        }

    }

}