package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun MyPage(
    onBackClick: () -> Unit,
    onStartClick: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val database: DatabaseReference = Firebase.database.reference.child("users")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 戻るボタン
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "戻る"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // タイトルとサブタイトル
        Text(
            text = "ユーザー名",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "ユーザー名は後から変更できます",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ユーザー名入力フィールド
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ユーザー名") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 保存ボタン
        Button(
            onClick = {
                currentUser?.email?.let { email ->
                    if (username.isNotEmpty()) {
                        // Firebaseにユーザー名を保存する処理
                        database.child(email.replace('.', ',')).child("username").setValue(username)
                            .addOnSuccessListener {
                                onStartClick(username)
                            }
                            .addOnFailureListener { e ->
                                // 保存失敗時の処理
                                // 例: エラーメッセージをログに記録する
                                Log.e("Firebase", "Failed to save username", e)
                            }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "保存する")
        }
    }
}
