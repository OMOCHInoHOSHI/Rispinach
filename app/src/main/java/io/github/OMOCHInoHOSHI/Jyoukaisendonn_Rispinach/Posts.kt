package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Posts(pName: Int, lName: String, cName: String) {
    val scope = rememberCoroutineScope()
    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = pName),
                    contentDescription = "プロフィール画像",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // 必要に応じて高さを調整
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    OutlinedText(
                        text = "名前: $lName",
                        stroke = Stroke(width = 4.0f),
                        textStyle = TextStyle(fontSize = 20.sp),
                        strokeColor = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    IconButton(onClick = { openBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "情報",
                            modifier = Modifier
                                .border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                            tint = Color(0xFFB0BEC5)
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Conversation(
                        messages = SampleData.conversationSample,
                        modifier = Modifier.weight(1f)
                    )
                    var text by rememberSaveable { mutableStateOf("") }
                    MessageInput(
                        text = text,
                        onTextChange = { text = it }
                    )
                }
                if (openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openBottomSheet = false },
                        sheetState = bottomSheetState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("場所:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cName,
                                textDecoration = TextDecoration.Underline,
                                color = Color.Blue,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.clickable { /* 必要に応じてクリック処理を追加 */ }
                            )

                            //通報場所
                            Text("通報:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cName,
                                textDecoration = TextDecoration.Underline,
                                color = Color.Blue,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.clickable { /* 必要に応じてクリック処理を追加 */ }
                            )
                        }
                    }
                }
            }
        }
    )
}
