package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Posts(pName: Bitmap, lName: String, Title: String, location: String, discoveryDate: String, idName: Int) {
    val scope = rememberCoroutineScope()
    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    )

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
                    OutlinedText(
                        text = "名前: $lName",
                        stroke = Stroke(width = 200.0f),      // 縁取りの太さ
                        textStyle = TextStyle(fontSize = 20.sp),
                        strokeColor = Color.White,
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
                    modifier = Modifier
                        .fillMaxSize()

                )
                {
                    Box(
                        modifier = Modifier
                        .clickable { isExpanded = !isExpanded },
                    )
                    {
                        //投稿時コメント(仮)
                        Surface(
                            //shape = MaterialTheme.shapes.medium,
                            shadowElevation = 10.dp,
                            //color = surfaceColor,
                            modifier = Modifier
                                .animateContentSize()
                                .padding(1.dp)
                        ) {
                            Text(
                                //表示と折り返しのテスト
                                text = Title,
                                modifier = Modifier
                                    .padding(all = 4.dp)
                                    .fillMaxWidth(),
                                fontSize = 30.sp,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                style = MaterialTheme.typography.bodyLarge,
                                overflow = TextOverflow.Ellipsis, // 非表示部分を省略記号に

                                )
                            // Boxの底辺に線を追加
                            Divider(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter) // 底辺に配置
                                    .fillMaxWidth(), // 横幅いっぱいに線を引く
                                thickness = 2.dp, // 線の太さ
                                color = Color.Black // 線の色
                            )
                            Divider(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth(), // 横幅いっぱいに線を引く
                                thickness = 2.dp, // 線の太さ
                                color = Color.Black // 線の色
                            )
                        }
                    }
//                    Text(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",//表示と折り返しのテスト
//                        fontSize = 30.sp,
//                    )
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    )
                    {
//                    Box(
//                        modifier = Modifier,
//                        contentAlignment = Alignment.TopStart
//                    )
//                    {
//                        Text(
//                            text = "test",
//                            fontSize = 30.sp,
//                        )
//                    }
//                        Conversation(
//                            messages = SampleData.conversationSample,
//                            modifier = Modifier.weight(1f)
//                        )
                        var text by rememberSaveable { mutableStateOf("") }
                Conversation(postId = idName.toString())


                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.CenterEnd
                    )
                    {
                        MessageInput(
                            text = text,
                            onTextChange = { text = it },
                            postId =idName.toString()
                        )
                    }
//                        Box(
//                            modifier = Modifier,
//                            contentAlignment = Alignment.CenterEnd
//                        )
//                        {
//                            MessageInput(
//                                text = text,
//                                onTextChange = { text = it },
//                                postId =idName.toString()
//                            )
//                        }
//                    Column {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Bottom
//                ) {
//                    //チャット表示ここでいいはず
//                    //Conversation(postId = idName.toString())
//
//                    }
                    }
                }
//                    var text by rememberSaveable { mutableStateOf("") }
//
////                    Box(
////                        modifier = Modifier,
////                        contentAlignment = Alignment.CenterEnd
////                    ) {
////                        MessageInput(
////                            text = text,
////                            onTextChange = { text = it },
////                            postId = idName.toString()
////                        )
////                    }
//                }
                if (openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openBottomSheet = false },
                        sheetState = bottomSheetState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 発見場所
                            Text("【 発見場所 】", style = MaterialTheme.typography.titleMedium)
                            Text("   $location\n", style = MaterialTheme.typography.titleMedium)

                            // 発見日
                            Text("【 発見日 】", style = MaterialTheme.typography.titleMedium)
                            Text("   $discoveryDate\n", style = MaterialTheme.typography.titleMedium)

                            // 通報場所
                            Text("【 通報 】", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                buildAnnotatedString() {
                                    append("")
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
                                    )
                                    {
                                        append("   地方環境事務所等一覧")
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

