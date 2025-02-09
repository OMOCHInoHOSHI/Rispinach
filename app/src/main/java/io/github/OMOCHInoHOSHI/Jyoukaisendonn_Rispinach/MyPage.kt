package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun MyPage(
    onBackClick: () -> Unit,
    onStartClick: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var currentUsername by remember { mutableStateOf("") }
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val database: DatabaseReference = Firebase.database.reference.child("users")
    val NLength=15

    // コンテキストの取得
    val context = LocalContext.current
    // 保存中かどうかを管理する
    var isSaving by remember { mutableStateOf(false) }

    // 現在のユーザー名を取得する
    LaunchedEffect(currentUser) {
        currentUser?.email?.let { email ->
            val userRef = database.child(email.replace('.', ',')).child("username")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedUsername = snapshot.getValue(String::class.java)
                    if (fetchedUsername != null) {
                        currentUsername = fetchedUsername
                        username = fetchedUsername // 入力フィールドに初期値として設定
                    } else {
                        Log.e("Firebase", "Username not found for user: $email")
                        Toast.makeText(context, "ユーザー名が見つかりませんでした", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to fetch username", error.toException())
                    Toast.makeText(context, "ユーザー名の取得に失敗しました", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            // ユーザーがログインしていない場合のフィードバック
            Toast.makeText(context, "ユーザーが認証されていません", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 戻るボタンとページタイトルを配置するRow
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(
//                onClick = onBackClick,
//                modifier = Modifier.size(24.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = "戻る",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "マイページ",
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }

        Spacer(modifier = Modifier.height(16.dp))

        // 現在のユーザー名の表示を上部に配置
        Text(
            text = "現在のユーザー名: $currentUsername",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 中央のコンテンツ
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // タイトルとサブタイトル
            Text(
                text = "ユーザー名の変更",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "ユーザー名は後から変更できます",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ユーザー名入力フィールド
            OutlinedTextField(
                value = username,
                onValueChange = {
//                    username = it
                    if(it.length<=NLength)
                    {
                        username = it
                    }
                                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("新しいユーザー名($NLength 文字まで)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "ユーザーアイコン",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 保存ボタン
            Button(
                onClick = {
                    currentUser?.email?.let { email ->
                        if (username.isNotEmpty()) {
                            isSaving = true // 保存開始
                            // Firebaseにユーザー名を保存する処理
                            database.child(email.replace('.', ',')).child("username").setValue(username)
                                .addOnSuccessListener {
                                    isSaving = false // 保存完了
                                    // Toastで成功メッセージを表示
                                    Toast.makeText(context, "ユーザー名を保存しました", Toast.LENGTH_SHORT).show()
                                    currentUsername = username // 表示を更新
                                    onStartClick(username)
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false // 保存失敗
                                    // Toastでエラーメッセージを表示
                                    Toast.makeText(context, "ユーザー名の保存に失敗しました", Toast.LENGTH_SHORT).show()
                                    // ログにも記録
                                    Log.e("Firebase", "Failed to save username", e)
                                }
                        } else {
                            // ユーザー名が空の場合のフィードバック
                            Toast.makeText(context, "ユーザー名を入力してください", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        // ユーザーがログインしていない場合のフィードバック
                        Toast.makeText(context, "ユーザーが認証されていません", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isSaving, // 保存中はボタンを無効化
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    text = "保存する",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
