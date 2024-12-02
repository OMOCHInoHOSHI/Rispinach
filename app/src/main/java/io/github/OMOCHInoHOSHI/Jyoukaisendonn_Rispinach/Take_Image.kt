package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

fun image_Uri(uri_get: Uri): Uri {


    if(uri_get != Uri.EMPTY){
        println("URIがヌルではない")

        return uri_get
    }
    else{
        println("URIがヌル")
        return  uri_get
    }
}
