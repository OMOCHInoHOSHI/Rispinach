package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PreviewCamera(
    //従来のクラスによるUIのプレビュービューを返すラムダ
    createPreviewCamera: (Context) -> PreviewView
){
    //createPreviesCameraを実行、PreviewViewを返す
    //多くの組み込みコンポーザーと同様に、modifierパラメータを受け取る
    AndroidView(
        factory = { ctx ->
            createPreviewCamera(ctx)
        },
        //fillMaxSizeで、PreviewViewを全体に表示されるようにする
        modifier = Modifier.fillMaxSize()
    )
}
