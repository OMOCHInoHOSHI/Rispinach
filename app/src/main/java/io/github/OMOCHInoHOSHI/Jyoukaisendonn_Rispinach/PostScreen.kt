package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(bitmap: Bitmap?) {
    // 投稿タイトルの状態を保持する変数
    var title by rememberSaveable { mutableStateOf("") }
    // 生物名の状態を保持する変数
    var speciesName by rememberSaveable { mutableStateOf("") }
    // 発見場所の状態を保持する変数
    var location by rememberSaveable { mutableStateOf("") }
    // 発見日付の状態を保持する変数
    var discoveryDate by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            // トップバーの設定
            TopAppBar(
                title = { Text("投稿準備画面") }
            )
        },
        floatingActionButton = {
            // 投稿ボタンの設定
            FloatingActionButton(
                onClick = { /* TODO: 投稿機能を実装 */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Post")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 画像エリアの設定
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Text(
                        text = "picture",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // タイトル入力フィールドの設定
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("投稿タイトル入力") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // 生物名入力フィールドの設定
            OutlinedTextField(
                value = speciesName,
                onValueChange = { speciesName = it },
                placeholder = { Text("生物名入力") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // 発見場所入力フィールドの設定
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("発見場所") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // 発見日付入力フィールドの設定
            OutlinedTextField(
                value = discoveryDate,
                onValueChange = { discoveryDate = it },
                placeholder = { Text("発見日付") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
}