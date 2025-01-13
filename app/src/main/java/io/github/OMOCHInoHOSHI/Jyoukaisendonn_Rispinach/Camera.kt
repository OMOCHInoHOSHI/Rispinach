package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraphBuilder

@Composable
fun Camera()
{
//    var isCameraOpen by remember { mutableStateOf(false) }

    var camera_flg = 1
//    camera_flg = CameraScreen_2(1)


    var showCamera by remember { mutableStateOf(true) } // カメラ表示フラグ

    if (showCamera) {

//        BackHandler {
//            println("BackHandler")
//            showCamera = false // カメラ画面を閉じる
//        }

        Dialog(
            onDismissRequest = { var showPopup = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // 幅を制限しない
            ) {
            // バックボタン処理を追加
//            BackHandler {
//                println("BackHandler")
//                showCamera = false // カメラ画面を閉じる
//            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                camera_flg = CameraScreen_2(1)

                // バック操作でカメラ終了
                BackHandler {
                    println("BackHandler")
                    showCamera = false // カメラ画面を閉じる
                }
            }
        }
    } else {

        // カメラ画面を閉じた後の処理（元の画面）
        Home()

    }

    }


    //val permissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)




    //デバッグ用
//    Text("main/camera")
//    println("Camera")
//}

//fun StartCamera()
//{
//    //カメラ起動
//}
//
//fun StartPhoto()
//{
//    //スマホ内の画像で判定
//}