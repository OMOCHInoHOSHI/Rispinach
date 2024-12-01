package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

        FloatingActionButton(
            onClick = { /*do something*/ },
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


//@Composable
//fun TakePhoto(takePhoto:() -> Unit){
//
//    Box(
//        modifier = Modifier
//            .size(60.dp)               // ボタンのサイズを正方形に
//            .clip(CircleShape)         // 真円にクリップ
//    ) {
//        FilledTonalButton(onClick = {  },
//            modifier = Modifier
//                .clip(CircleShape)
//                .align(Alignment.BottomCenter)// 下部中央に配置
//                .offset(x = (-20).dp)   //左に
//        ){
//            Icon(
//                imageVector = Icons.Rounded.Photo, // フォルダアイコン
//                contentDescription = "",
//                modifier = Modifier
//                    .size(30.dp)                  // アイコンのサイズ
//            )
//        }
//
//    }
//
//
//}