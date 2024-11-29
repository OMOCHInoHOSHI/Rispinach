package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//写真撮影のUIを作成
//TakePhotoコンポーザはボタンのUIを表示する
@Composable
fun TakePhoto(takePhoto:() -> Unit){

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        //takePhotoは、ラムダの引数を受け取る
        Button(onClick = takePhoto,
            modifier = Modifier
                .align(Alignment.BottomCenter)// 下部中央に配置
                .padding(bottom = 50.dp) // 下部からの余白
        ) {
            Text(text = "撮影")
            Icon(
                imageVector = Icons.Rounded.Camera, // カメラのアイコンに変更
                contentDescription = "カメラ起動",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }

}