package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

//パーミッションハンドラを使用してカメラの権限を要求するためのアクティビティリザルトランチャーを作成
@Composable
fun CameraScreen(){
    //状態保持
    var isGranted by remember { mutableStateOf(false) }

    PermissionHand { granted ->
        isGranted = granted
    }
    //Trueならカメラの権限が許可されている事を意味する
    if(isGranted){  //True
        Box{
            Text(text = "カメラの権限を取得できました")
            println("カメラの権限取得")
        }
    }else{
        Text(text = "カメラの権限がありません")
        println("カメラの権限が取得がありません")
    }
}