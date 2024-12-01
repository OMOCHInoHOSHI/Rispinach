package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
    ){

        //takePhotoは、ラムダの引数を受け取る
        FloatingActionButton(onClick = takePhoto,
            modifier = Modifier
                .align(Alignment.BottomCenter)// 下部中央に配置
                .padding(bottom = 50.dp) // 下部からの余白
                .clip(CircleShape)
        ) {
//            Text(text = "撮影")
            Icon(
                imageVector = Icons.Rounded.Camera, // カメラのアイコンに変更
                contentDescription = "カメラ起動",
//                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        FloatingActionButton(

            onClick = {
                },
            modifier = Modifier

                .align(Alignment.BottomCenter)// 下部中央に配置
                .padding(bottom = 50.dp) // 下部からの余白
                .absoluteOffset(x = (-64).dp)   // 左に配置
                .clip(CircleShape)
        ){
            Icon(Icons.Filled.Photo, contentDescription = "追加")
        }
    }

}
