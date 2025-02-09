//Chat.kt
package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Message(val user: String = "", val body: String = "", val timestamp: Long = 0L)

val auth = FirebaseAuth.getInstance()
val currentUser = auth.currentUser

@Composable
fun Conversation(postId: String, modifier: Modifier = Modifier) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by rememberSaveable { mutableStateOf("") }  // 入力されたメッセージを保持

    // Firebase Realtime Database からコメントをリアルタイムで取得
    // Firebase Realtime Database からコメントをリアルタイムで取得
    LaunchedEffect(postId) {
        val database = Firebase.database.reference
        val postsRef = database.child("posts").child(postId).child("comments")
        val usersRef = database.child("users")

        // ユーザー情報を保持するマップ
        val usersMap = mutableMapOf<String, String>()

        // usersノードからユーザー情報を取得してマップに保存
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val storedKey = userSnapshot.key // Firebaseに保存されているカンマ区切りのメールアドレス
                    val originalEmail = storedKey?.replace(",", ".") // カンマをドットに戻す
                    val username = userSnapshot.child("username").getValue(String::class.java)

                    if (originalEmail != null && username != null) {
                        usersMap[originalEmail] = username
                    }
                }

                // メッセージのリアルタイム取得
                postsRef.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val messageMap = snapshot.value as? Map<String, Any>
                        if (messageMap != null) {
                            val userEmail = messageMap["user"] as? String ?: ""
                            val formattedEmail = userEmail.replace(".", ",")  // 照合用に.を,に変換
                            val username = usersMap[userEmail] ?: usersMap[formattedEmail] ?: "Unknown User"

                            val body = messageMap["body"] as? String ?: ""
                            val timestamp = messageMap["timestamp"] as? Long ?: 0L
                            val message = Message(username, body, timestamp)

                            messages = messages + message
                            Log.d("Firebase", "Parsed message added: $message")
                        } else {
                            Log.e("Firebase", "Failed to parse snapshot: ${snapshot.value}")
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Failed to read comments data.", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read users data.", error.toException())
            }
        })
    }


//    // コメントリストの表示
//    LazyColumn(
////        modifier = modifier//.padding(bottom = 80.dp)
////            .height(560.dp)
//        modifier = Modifier.weight(1f), // 残りの空間を全て使用
//        contentPadding = PaddingValues(bottom = 8.dp)
//    )
//    {
//        items(messages) { message ->
//            MessageCard(message)
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // コメントリストの表示
        LazyColumn(
//        modifier = modifier//.padding(bottom = 80.dp)
//            .height(560.dp)
            modifier = Modifier.weight(1f), // 残りの空間を全て使用
            contentPadding = PaddingValues(bottom = 8.dp)
        )
        {
            items(messages) { message ->
                MessageCard(message)
            }
        }
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
            .clickable {}
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
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
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
        // テキストフィールドを親コンポーネントの幅に固定
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)  // 幅を親コンポーネントの残りの空間に合わせる
                .padding(2.dp),
            maxLines = 5,
            singleLine = false,
            label = { Text("メッセージを入力") }
        )

        // 投稿ボタン
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),  // 適切な余白を設定
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
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Log.e("Firebase", "ユーザーがログインしていません")
        return
    }

    val userEmail = user.email ?: "Unknown User"

    val commentData = mapOf(
        "user" to userEmail,
        "body" to message,  // "message" ではなく "body" に変更
        "timestamp" to ServerValue.TIMESTAMP
    )

    val commentsRef = Firebase.database.reference.child("posts").child(postId).child("comments")
    commentsRef.push().setValue(commentData).addOnSuccessListener {
        Log.d("Firebase", "Comment added successfully: $commentData")
    }.addOnFailureListener {
        Log.e("Firebase", "Failed to add comment", it)
    }
}




@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    stroke: Stroke = Stroke(),
    strokeColor: Color = Color.Transparent,
) {
    var textLayoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    BasicText(
        text = text,
        style = textStyle,
        onTextLayout = { textLayoutResult = it },
        modifier = modifier
            .padding(4.dp)
            .drawBehind {
                textLayoutResult?.let {
                    drawText(
                        textLayoutResult = it,
                        color = strokeColor,
                        //style = textStyle.copy(drawStyle = stroke)
                    )
                }
            }
    )
}
