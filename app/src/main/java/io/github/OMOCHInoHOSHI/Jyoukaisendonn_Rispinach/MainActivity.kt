package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chaquo.python.Python
import io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach.ui.theme.RispinachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pythonにより追加
        val py = Python.getInstance()
        val module = py.getModule("jikken")
        val tex1 = module.callAttr("hello_world")
        println(tex1)
        
        enableEdgeToEdge()
        setContent {
            RispinachTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "print_py",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                //カメラ権限呼び出し
                CameraScreen()
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