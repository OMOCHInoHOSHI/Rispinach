package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
// map表示
fun MapContent() {
    val defaultPosition = LatLng(35.689501, 139.691722) // 東京都庁
    val defaultZoom = 8f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, defaultZoom)
    }
    Row {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth() // 画面幅いっぱいに広げる
                .height(300.dp) // 高さを300dpに固定 (調整可能)
            ,
            cameraPositionState = cameraPositionState,
        )

    }

}