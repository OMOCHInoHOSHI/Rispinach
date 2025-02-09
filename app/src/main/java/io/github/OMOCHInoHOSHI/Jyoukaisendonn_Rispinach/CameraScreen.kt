package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput


//パーミッションハンドラを使用してカメラの権限を要求するためのアクティビティリザルトランチャーを作成
//@Composable
//fun CameraScreen(){
//    //状態保持
//    var isGranted by remember { mutableStateOf(false) }
//    //カメラスクリーンを表示
//    val cameraState = remenbreCameraState()
//
//    PermissionHand { granted ->
//        isGranted = granted
//    }
//    if(isGranted){  //True
//        Box{
//            //Text(text = "カメラの権限を取得できました")
//            println("カメラの権限取得")
//            //作成したプレビューカメラコンポーザブルでビューを表示
//            PreviewCamera { ctx ->
//                cameraState.startCamera(ctx)
//            }
//            //撮影用のボタンを配置
//            TakePhoto {
//                cameraState.takePhoto()
//            }
//        }
//    }else{
//        Text(text = "カメラの権限がありません")
//        println("カメラの権限が取得がありません")
//    }
//}

@Composable
fun CameraScreen_2(flg: Int): Int{

    var currentFlg by remember { mutableStateOf(flg) }

    //状態保持
    var isGranted by remember { mutableStateOf(false) }
    //カメラスクリーンを表示
    val cameraState = remenbreCameraState()

    PermissionHand { granted ->
        isGranted = granted
    }
    if(isGranted){  //True
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // 背後のタッチイベントを無効化
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                }
        ){
            //Text(text = "カメラの権限を取得できました")
            println("カメラの権限取得")
            //作成したプレビューカメラコンポーザブルでビューを表示
            PreviewCamera { ctx ->
                cameraState.startCamera(ctx)
            }
            //撮影用のボタンを配置
            TakePhoto {
                cameraState.takePhoto2()
            }
            // 投稿準備画面に撮影した画像を渡す
            cameraState.bitmap_Camera()
        }
    }else{
        Text(text = "カメラの権限がありません")
        println("カメラの権限が取得がありません")
    }

    BackHandler{
        cameraState.stopCamera()
        currentFlg = 0
    }

    return currentFlg
}