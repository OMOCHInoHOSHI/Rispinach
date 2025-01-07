package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun image_Uri_to_Bitmap(uri_get: Uri):Bitmap{


//    if(uri_get != Uri.EMPTY){
//        println("URIがヌルではない")
//
//        return uri_get
//    }
//    else{
//        println("URIがヌル")
//        return  uri_get
//    }

    // ContentResolverを取得
    val contentResolver = LocalContext.current.contentResolver

    // InputStreamを取得
    val inputStream = contentResolver.openInputStream(uri_get)

    // Bitmapにデコード
    val bitmap = BitmapFactory.decodeStream(inputStream)

    return bitmap
}
