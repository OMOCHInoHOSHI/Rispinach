package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

//写真撮影のUIを作成
//TakePhotoコンポーザはボタンのUIを表示する
@Composable
fun TakePhoto(takePhoto:() -> Unit){
    //takePhotoは、ラムダの引数を受け取る
    Button(onClick = takePhoto) {
        Text(text = "撮影")
    }
}
