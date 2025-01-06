package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chaquo.python.Python
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
//import com.example.hs11.ui.theme.HS11Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach.ui.theme.RispinachTheme

import androidx.compose.foundation.layout.* // layout関連をまとめてimport
import androidx.compose.material.icons.rounded.* // roundedアイコンをまとめてimport
import androidx.compose.material3.* // Material3コンポーネントをまとめてimport
import androidx.compose.runtime.* // runtime関連をまとめてimport
import androidx.compose.ui.* // ui関連をまとめてimport

import android.util.Log
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch

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
                    val a = innerPadding
//                    Greeting(
//                        name = "print_py",
//                        modifier = Modifier.padding(innerPadding)
//                    )

                    Surface(
                        color = MaterialTheme.colorScheme.background
                    )
                    {
                        SideEffect { Log.d("compose-log", "Surface") }
                        DRAWER()
                    }

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
                    if (camera_flg == 1) {
//                        CameraScreen()
                        camera_flg = CameraScreen_2(camera_flg)
//                        camera_flg=0
                    }
                    //カメラボタンでカメラ起動E----------------------------------------------------
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DRAWER(
    modifier: Modifier = Modifier
        .padding(top = 20.dp, start = 30.dp),
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main" // mainに変更
) {
    SideEffect { Log.d("compose-log", "DRAWER") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        SideEffect { Log.d("compose-log", "Box") }
        NavHost(navController = navController, startDestination = startDestination)
        {
            mainScreen() // 先程の拡張関数 mainScreenを呼び出す
        }
    }
}

@Composable
fun MapContent() {
    // 地名と緯度経度の対応付け
    val locations = mapOf(
        "東京" to LatLng(35.689501, 139.691722), // 東京都庁
        "大阪" to LatLng(34.6937, 135.5023),   // 大阪府庁
        "京都" to LatLng(35.0116, 135.7681),   // 京都市役所
        "福岡" to LatLng(33.5890, 130.4020)    // 福岡市役所
    )
    val defaultPosition = locations["東京"]!! // 東京都庁
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 8f)
    }

    // CoroutineScopeをrememberで保持
    val coroutineScope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        )

        var expanded by remember { mutableStateOf(false) }
        val options = locations.keys.toList()
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "その他のオプション")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                            // 地名に対応する緯度経度を取得
                            val selectedLocation = locations[option]

                            // マップを移動
                            if (selectedLocation != null) {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedLocation,
                                            10f
                                        ), //ズームレベルも変更
                                        1000 // アニメーション時間（ミリ秒）
                                    )
                                }
                                Log.d("MapContent", "$option が選択されました")
                            }
                        }
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    RispinachTheme {
//        Greeting("Android")
//    }
//}