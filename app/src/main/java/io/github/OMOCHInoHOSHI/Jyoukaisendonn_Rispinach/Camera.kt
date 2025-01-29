package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// showCameraを変更するためのViewModelS------------------------
class CameraViewModel : ViewModel() {
    // showCamera の状態を管理
    private val _showCamera = mutableStateOf(true)
    val showCamera: State<Boolean> get() = _showCamera

    // showCamera を変更するメソッド
    fun setShowCamera(value: Boolean) {
        _showCamera.value = value
    }
}
// showCameraを変更するためのViewModelE------------------------


// カメラ～投稿画面S-----------------------------------------------------------------------------------
@Composable
fun Camera(cameraViewModel: CameraViewModel = viewModel())
{
//    var isCameraOpen by remember { mutableStateOf(false) }

    var camera_flg by rememberSaveable { mutableIntStateOf(1) }
//    camera_flg = CameraScreen_2(1)


//    var showCamera by rememberSaveable { mutableStateOf(true) } // カメラ表示フラグ

    // ViewModel から showCamera を参照
    val showCamera = cameraViewModel.showCamera.value

    val showDialog = remember { mutableStateOf(true) }

//    println("1 camera_flg = $camera_flg")
//    println("showCamera = $showCamera")
    if (showCamera) {

//        BackHandler {
//            println("BackHandler")
//            showCamera = false // カメラ画面を閉じる
//        }

        Dialog(
            onDismissRequest = { /*var showPopup*/showDialog.value = false },
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

                camera_flg = CameraScreen_2(camera_flg)
                println("Camera.ktcamera_flg = $camera_flg")

                // バック操作でカメラ終了
                BackHandler {
                    println("BackHandler")
//                    camera_flg = 0
//                    showCamera = false // カメラ画面を閉じる
                    cameraViewModel.setShowCamera(false)
                    //showDialog.value = false
//                    if(camera_flg == 0){
//                        showCamera = false // カメラ画面を閉じる
//                        println("2 showCamera = $showCamera")
//                    }
                }

//                if(camera_flg == 0){
//                    showCamera = false // カメラ画面を閉じる
//                    println("2 showCamera = $showCamera")
//                }
            }
        }
    } else {
        // カメラ画面を閉じた後の処理（元の画面）
        //showDialog.value=false
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.onBackPressed()
        //Home()
    }
}
// カメラ～投稿画面E-----------------------------------------------------------------------------------