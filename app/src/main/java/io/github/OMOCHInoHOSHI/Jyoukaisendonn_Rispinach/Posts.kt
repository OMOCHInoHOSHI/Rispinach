package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Posts(pName: Bitmap, lName: String, idName: Int) {
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
                    bitmap = pName.asImageBitmap(), // プロフィール画像を表示するためのBitmap
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
                    Text(
                        text = "名前: $lName",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(8.dp)
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    //チャット表示ここでいいはず
                    Conversation(postId = idName.toString())

                    var text by rememberSaveable { mutableStateOf("") }

                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        MessageInput(
                            text = text,
                            onTextChange = { text = it },
                            postId = idName.toString()
                        )
                    }
                }
                if (openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openBottomSheet = false },
                        sheetState = bottomSheetState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("通報", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                buildAnnotatedString() {
                                    append("地方環境事務所等一覧")
                                    withLink(
                                        LinkAnnotation.Url(
                                            "https://www.env.go.jp/nature/intro/reo.html",
                                            TextLinkStyles(
                                                style = SpanStyle(
                                                    color = Color.Blue,
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            )
                                        )
                                    ) {
                                        append("地方環境事務所等一覧")
                                    }
                                },
                                color = Color.Blue
                            )
                        }
                    }
                }
            }
        }
    )
}

