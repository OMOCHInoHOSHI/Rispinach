package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Report
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
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
fun Posts(pName: Bitmap, lName: String, Title: String, location: String, discoveryDate: String, Lat: Double?, Lng: Double?, idName: Int) {
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
                        .background(Color.DarkGray.copy(alpha = 0.5f)) // 半透明の灰色
                ) {
                    Text(
                        text = "AI判定：$lName",
                        style = TextStyle(
                            color = Color.White,  // テキストの色
                            fontSize = 15.sp,
                            shadow = Shadow(
                                color = Color.Black,  // 縁取りの色
                                offset = Offset(4f, 4f),  // 縁取りの位置（大きさ）
                                blurRadius = 16f  // 縁取りのぼかし具合（大きさ）
                            )
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
//                        Canvas(modifier = Modifier.matchParentSize()) {
//                            val path = Path().apply {
//                                moveTo(size.width / 2f, 0f) // 上端の中央
//                                lineTo(0f, size.height/1.25f) // 左下
//                                lineTo(size.width, size.height/1.25f) // 右下
//                                close() // 三角形を閉じる
//                            }
//                            drawPath(path, Color.Black) // 三角形を黒色で描画
//                        }
//                        Canvas(modifier = Modifier.matchParentSize()) {
//                            val centerX = size.width / 2f  // 中心X座標
//                            val centerY = size.height / 2f  // 中心Y座標
//                            val radius = size.width / 3f  // 半径（画面幅に基づいて設定）
//
//                            val path = Path()
//
//                            // 六角形の各頂点を計算してPathを作成
//                            for (i in 0 until 6) {
//                                val angle = Math.toRadians((60 * i).toDouble())  // 60度ずつ回転
//                                val x = (centerX + radius * Math.cos(angle)).toFloat()
//                                val y = (centerY + radius * Math.sin(angle)).toFloat()
//
//                                if (i == 0) {
//                                    path.moveTo(x, y)  // 最初の点でPathを開始
//                                } else {
//                                    path.lineTo(x, y)  // 他の点に線を引く
//                                }
//                            }
//
//                            path.close()  // 最後の点を最初の点と繋げて閉じる
//
//                            drawPath(path, Color.Red)  // 黒色で六角形を描画
//                        }
//                        Canvas(modifier = Modifier.matchParentSize()) {
//                            val centerX = size.width / 2f  // 中心X座標
//                            val centerY = size.height / 2f  // 中心Y座標
//                            val radius = size.width / 3f  // 半径（画面幅に基づいて設定）
//
//                            val path = Path()
//
//                            // 八角形の各頂点を計算してPathを作成
//                            for (i in 0 until 8) {
//                                val angle = Math.toRadians((45 * i).toDouble())  // 45度ずつ回転
//                                val x = (centerX + radius * Math.cos(angle)).toFloat()
//                                val y = (centerY + radius * Math.sin(angle)).toFloat()
//
//                                if (i == 0) {
//                                    path.moveTo(x, y)  // 最初の点でPathを開始
//                                } else {
//                                    path.lineTo(x, y)  // 他の点に線を引く
//                                }
//                            }
//
//                            path.close()  // 最後の点を最初の点と繋げて閉じる
//
//                            drawPath(path, Color.Red)  // 黒色で八角形を描画
//                        }

                        Canvas(modifier = Modifier.matchParentSize()) {
                            val centerX = size.width / 2f  // 中心X座標
                            val centerY = size.height / 2f  // 中心Y座標
                            val radius = size.width / 3f  // 半径（画面幅に基づいて設定）

                            val path = Path()

                            // 45度回転のためのラジアン変換
                            val rotationAngle = Math.toRadians(22.5)  // 22.5度をラジアンに変換

                            // 八角形の各頂点を計算してPathを作成
                            for (i in 0 until 8) {
                                // 45度ずつ回転させた角度を計算
                                val angle = Math.toRadians((45 * i).toDouble())

                                // 回転前のx, y座標を計算
                                val xBeforeRotation = centerX + radius * Math.cos(angle)
                                val yBeforeRotation = centerY + radius * Math.sin(angle)

                                // 回転行列を使って座標を回転させる
                                val x = (xBeforeRotation - centerX) * Math.cos(rotationAngle) - (yBeforeRotation - centerY) * Math.sin(rotationAngle) + centerX
                                val y = (xBeforeRotation - centerX) * Math.sin(rotationAngle) + (yBeforeRotation - centerY) * Math.cos(rotationAngle) + centerY

                                // 最初の点でPathを開始
                                if (i == 0) {
                                    path.moveTo(x.toFloat(), y.toFloat())
                                } else {
                                    path.lineTo(x.toFloat(), y.toFloat())  // 他の点に線を引く
                                }
                            }

                            path.close()  // 最後の点を最初の点と繋げて閉じる

                            drawPath(path, Color(0xFFFF663C))  // 八角形を描画
                        }




                        Icon(
                            imageVector =Icons.Outlined.Report,
                            contentDescription = "情報",
                            modifier = Modifier
                                .padding(top=1.dp, start = 1.dp/*bottom=2.dp,end=2.dp*/),
                                //.background(color = Color.Black, shape = ),
                                //.border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                            tint = Color.Black
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
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded },
                    ) {
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
                                lineHeight=32.sp, // 行間を32spに設定
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        var text by rememberSaveable { mutableStateOf("") }
                        Conversation(postId = idName.toString())

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
                }
                if (openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openBottomSheet = false },
                        sheetState = bottomSheetState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 発見場所
                            Text("【 発見場所 】", style = MaterialTheme.typography.titleMedium)
//                            Text("  $location\n", style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp))
                            Text(
                                text = "  $location\n",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                modifier = Modifier.clickable {     // クリック時の処理
//                                    MapMarkers(Lat, Lng)        // マーカー付き地図
                                },
                                color = Color.Blue
                            )

                            // 発見日
                            Text("【 発見日 】", style = MaterialTheme.typography.titleMedium)
                            Text("  $discoveryDate\n", style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp))

                            // 通報場所
                            Text("【 通報 】", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                buildAnnotatedString {
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
                                    ) {
                                        append("  地方環境事務所等一覧")
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

