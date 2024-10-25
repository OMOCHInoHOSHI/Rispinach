package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach


import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

//カメラの状態を表すデータクラスとして用意
data class CameraState (
    //コンストラクタで必要なパラメータを受け取り、これを通じてカメラ関連の操作を行う
    val context: Context,
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    val LifeCycleOwne: LifecycleOwner,
    val imageCapture: ImageCapture
){
    //カメラを起動するためのメソッド   //Contextを受け取り、PreviewViewオブジェクトを返す
    fun startCamera(ctx:Context):PreviewView{
        //previewViewオブジェクトを生成し、インプリメンテーションモードをコンパチブルに設定
        //他にもパフォーマンスモードがある（一部の機能が制限されてる）
        val previewView = PreviewView(ctx).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }

        //コンテキストに関連されたメインメインスレッドでキューに入れられたタスクを実行するエグゼキューターを返す
        val executor = ContextCompat.getMainExecutor(ctx)
        //アプリコンポーネントの呼び出しを実行するスレッド

        //カメラプロバイダーの初期化が完了した時に呼び出されるリスナーを登録
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also{
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            //カメラプロバイダを取得、プレビューを設定 バックカメラをデフォで選択
            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            //bindライフサイクル関数で、プレビューとイメージキャプチャのユースケースをカメラに関連付ける
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    LifeCycleOwne,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }catch (e:Exception){
                //エラーが発生した場合はログ
                Log.e("CameraPreview","Use case binding failed",e)
            }
        },executor
        )
        //完成したビューをリターンで戻す
        return  previewView
    }
}

//UI構築の際に、カメラ関連の状態を管理するための関数
@Composable
fun remenbreCameraState(
    //カメラで使用するコンテキストを取得
    context: Context = LocalContext.current,
    //現在のプロセスに関連するプロセスカメラプロバイダのインスタンスを取得    //実際のカメラの処理
    caemraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context),
    //現在のライフサイクルオーナー
    //プレビューとイメージキャプチャのユースケースをカメラにバインドする時に使用 ライフサイクル管理をカメラが管理
    LifeCycleOwne: LifecycleOwner  = LocalLifecycleOwner.current,       //見本と少し違う変数名(?)
    //高解像度かつ高品質の写真をキャプチャできるカメラのコンポーネント
    imageCapture: ImageCapture = ImageCapture.Builder().build()
    //リメンバーを使い、CameraStateインスタンスをComposable関数内で状態を保持し利用できる
) = remember(context, caemraProviderFuture, LifeCycleOwne){
    //引数はキーになっている　変更があった場合はキャッシュを削除し、新たに作成
    CameraState(
        context = context,
        cameraProviderFuture = caemraProviderFuture,
        LifeCycleOwne  = LifeCycleOwne,
        imageCapture = imageCapture
    )
}
