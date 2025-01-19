//Chat.kt
package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Message(val user: String = "", val body: String = "", val timestamp: Long = 0L)

@Composable
fun Conversation(postId: String, modifier: Modifier = Modifier) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by rememberSaveable { mutableStateOf("") }  // 入力されたメッセージを保持

    // Firebase Realtime Database からコメントをリアルタイムで取得
    LaunchedEffect(postId) {
        val database = Firebase.database.reference.child("posts").child(postId).child("comments")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                Log.d("FirebaseData", "Received message: $message")  // ログを追加
                message?.let {
                    messages = messages + it // 正しくメッセージをリストに追加
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java)
                updatedMessage?.let {
                    messages = messages.map { message ->
                        if (message.timestamp == updatedMessage.timestamp) {
                            updatedMessage // 更新されたメッセージをリストに反映
                        } else {
                            message
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedMessage = snapshot.getValue(Message::class.java)
                removedMessage?.let {
                    messages = messages.filterNot { message -> message.timestamp == removedMessage.timestamp }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // コメントリストの表示
    LazyColumn(modifier = modifier.padding(bottom = 80.dp)) {
        items(messages) { message ->
            MessageCard(message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // メッセージ入力フォーム
        MessageInput(
            text = text,
            onTextChange = { text = it },
            postId = postId  // 投稿IDを渡す
        )
    }
}

@Composable
fun MessageCard(msg: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    )

    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = msg.user,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                // bodyが空の場合は "コメントがありません" を表示
                Text(
                    text = if (msg.body.isNotEmpty()) msg.body else "コメントがありません",
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MessageInput(text: String, onTextChange: (String) -> Unit, postId: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(8.dp)  // 必要なパディング
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,  // onTextChangeを渡す
            modifier = modifier
                .width(300.dp)
                .padding(8.dp),
            maxLines = 5,
            singleLine = false,
            label = { Text("メッセージを入力") }
        )

        // 投稿ボタン
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically),
            onClick = {
                if (text.isNotEmpty()) {
                    postComment(postId, text)  // コメント投稿処理を呼び出し
                    onTextChange("")  // 入力フィールドをクリア
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "投稿",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF2196F3)
            )
        }
    }
}

fun postComment(postId: String, message: String) {
    val user = "ペルソナユーザ"  // 現在のユーザーIDを使用
    val commentData = mapOf(
        "user" to user,
        "body" to message,  // bodyにコメントを格納
        "timestamp" to ServerValue.TIMESTAMP
    )

    val commentsRef = Firebase.database.reference.child("posts").child(postId).child("comments")
    commentsRef.push().setValue(commentData)  // コメントを追加
}


