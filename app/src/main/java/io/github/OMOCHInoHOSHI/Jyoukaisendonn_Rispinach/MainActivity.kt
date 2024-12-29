package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chaquo.python.Python
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach.ui.theme.RispinachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pythonにより追加
        val py = Python.getInstance()
        val module = py.getModule("jikken")
        val tex1 = module.callAttr("hello_world")
        println(tex1)

        //enableEdgeToEdge()    //スマホの端を無くす
        setContent {



            RispinachTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "print_py",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MapMarkers()        // マーカー

                //カメラボタンでカメラ起動S----------------------------------------------------
                var camera_flg by remember { mutableIntStateOf(0) } // flg の状態を管理する
                FilledTonalButton(
                    onClick = { camera_flg = 1 },
                    modifier = Modifier.size(80.dp).padding(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoCamera, // カメラのアイコンに変更
                        contentDescription = "カメラ起動",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
                if(camera_flg==1){
//                        CameraScreen()
                    camera_flg = CameraScreen_2(camera_flg)
                    MapContent()
//                        camera_flg=0
                }
                //カメラボタンでカメラ起動E----------------------------------------------------
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RispinachTheme {
        Greeting("Android")
    }
}