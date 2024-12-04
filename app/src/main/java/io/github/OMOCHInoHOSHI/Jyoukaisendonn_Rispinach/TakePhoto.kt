package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


//写真撮影のUIを作成
//TakePhotoコンポーザはボタンのUIを表示する
@Composable
fun TakePhoto(takePhoto:() -> Unit){

    // フォトピックフラグ
    var select_flg by remember { mutableStateOf(false) }
    // バックカメラ切り替えフラグ
    var backcamera_flg by remember { mutableStateOf(true) }
    // フラッシュ切り替え
    var flashmode_flg by remember { mutableIntStateOf(0) }
    // フラッシュモード
    var flashmode = Icons.Filled.FlashAuto

    //デフォでオフ
    if(flashmode_flg == 0){
        flashmode = Icons.Filled.FlashOff
    }
    else if(flashmode_flg == 1){
        flashmode = Icons.Filled.FlashOn
    }
    else if(flashmode_flg ==2){
        flashmode = Icons.Filled.FlashAuto
    }

    // UI-S-------------------------------------------------------------------------------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
    ){

        Row (
            modifier = Modifier
                .align(Alignment.BottomCenter) // Box内でRowを下部中央に配置
                .padding(bottom = 16.dp), // 画面下部からの余白を設定
            horizontalArrangement = Arrangement.spacedBy(30.dp), // ボタン間のスペースを設定

//            verticalAlignment = Alignment.CenterVertically // ボタンを縦方向で中央揃え
//            horizontalArrangement = Arrangement.Center,  //水平方向の中央
//            verticalAlignment = Alignment.Bottom       //垂直方向の下
        )
        {
            // フォトピッカーボタンS-------------------------------------------------------------
            FloatingActionButton(

                onClick = {select_flg = true},
                modifier = Modifier

//                    .align(Alignment.BottomCenter)// 下部中央に配置
//                    .padding(bottom = 50.dp) // 下部からの余白
//                    .absoluteOffset(x = (-64).dp)   // 左に配置
                    .clip(CircleShape)
            ){
                Icon(Icons.Filled.Photo, contentDescription = "追加")
            }
            // フォトピッカーボタンS-------------------------------------------------------------

            //シャッターボタンS---------------------------------------------------------------
            //takePhotoは、ラムダの引数を受け取る
            FloatingActionButton(onClick = takePhoto,
                modifier = Modifier
//                    .align(Alignment.BottomCenter)// 下部中央に配置
//                    .padding(bottom = 50.dp) // 下部からの余白
                    .clip(CircleShape)
            ) {
//            Text(text = "撮影")
                Icon(
                    imageVector = Icons.Rounded.Camera, // カメラのアイコンに変更
                    contentDescription = "カメラ起動",
//                modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
            //シャッターボタンE---------------------------------------------------------------

            // フロント・バックカメラチェンジボタンS--------------------------------------------------
            FloatingActionButton(
                onClick = {
                    if(backcamera_flg == true){
                        backcamera_flg = false
                    }
                    else{
                        backcamera_flg = true
                    }
                },
                modifier = Modifier
//                    .align(Alignment.BottomCenter)// 下部中央に配置
//                    .padding(bottom = 50.dp) // 下部からの余白
                    .clip(CircleShape)
            ){
                Icon(Icons.Filled.FlipCameraAndroid, contentDescription = "バックカメラ切り替え")
            }
            // フロント・バックカメラチェンジボタンE--------------------------------------------------

        }

        //フラシュボタンS-------------------------------------------------------------------
        FloatingActionButton(
            onClick = {
                flashmode_flg++
                // flashflgループ
                if (flashmode_flg >= 3) {
                    flashmode_flg = 0
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .clip(CircleShape)
        ) {
            Icon(flashmode, contentDescription = "フラッシュオフ")
        }
        //フラシュボタンE-------------------------------------------------------------------

        //

        // UI-E-------------------------------------------------------------------------------------------------------------------------------

    }


    //ボタンが押されたらフォトピッカー起動S-----------------------------------------------
    if(select_flg != false){

        println("選択")

        photosPick2(onNothingSelected = {
            // Handle nothing selected, e.g., show a message or log an event
            Log.d("MainActivity", "No image selected")
            select_flg = false
        })
    }
    //ボタンが押されたらフォトピッカー起動E-----------------------------------------------
}
