package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(): Boolean {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var password by remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    var rememberMe by remember { mutableStateOf(sharedPreferences.getBoolean("rememberMe", false)) }
    var isLoading by remember { mutableStateOf(false) }

    var signSuccess by remember { mutableStateOf(false) }

    // GoogleSignInClient 設定
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1:796232278012:web:e07084ff3d13d8e4a6dc79") // FirebaseのWebクライアントIDを設定
                .requestEmail()
                .build()
        )
    }

    val googleSignInResult = rememberUpdatedState { task: Task<GoogleSignInAccount> ->
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Googleログイン成功", Toast.LENGTH_SHORT).show()
                            signSuccess = true
                        } else {
                            Toast.makeText(context, "Google認証に失敗しました", Toast.LENGTH_SHORT).show()
                            signSuccess = false
                        }
                    }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google認証に失敗: ${e.message}", Toast.LENGTH_SHORT).show()
            signSuccess = false
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            googleSignInResult.value(task)
        }
    )

    // 自動ログイン処理
    if (rememberMe && email.isNotEmpty() && password.isNotEmpty()) {
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                signSuccess = task.isSuccessful
                if (task.isSuccessful) {
                    Toast.makeText(context, "自動ログイン成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "自動ログイン失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 110.dp)
                    .fillMaxWidth(),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.offset(x = -15.dp),
                            text = "ようこそRispinachへ",
                            fontSize = 30.sp
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("メールアドレス") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("パスワード") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ///*
                // 「ログイン情報を保持する」チェックボックス
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(
//                        checked = rememberMe,
//                        onCheckedChange = { rememberMe = it }
//                    )
//                    Text("ログイン情報を保持する")
//                }

                Spacer(modifier = Modifier.height(16.dp))

                // ログインボタン
                Button(
                    onClick = {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show()
                                    signSuccess = true
                                    if (rememberMe) {
                                        with(sharedPreferences.edit()) {
                                            putString("email", email)
                                            putString("password", password)
                                            putBoolean("rememberMe", true)
                                            apply()
                                        }
                                    } else {
                                        with(sharedPreferences.edit()) {
                                            clear()
                                            apply()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "ログイン失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    signSuccess = false
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("ログインする", fontSize = 18.sp)
                }
                 //*/

                Spacer(modifier = Modifier.height(16.dp))

                // 新規登録ボタン
                Button(
                    onClick = {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "ユーザー登録成功", Toast.LENGTH_SHORT).show()
                                    signSuccess = true
                                } else {
                                    Toast.makeText(context, "登録失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    signSuccess = false
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("新規登録", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Googleログインボタン
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Google アカウントでログイン", fontSize = 18.sp, color = Color.White)
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    )

    return signSuccess
}



/**
 * 例: Firestore や Realtime Database にユーザー情報を保存する場合の関数。
 * 実際に使う時は必要なパラメータを調整し、マルチラインコメントを正しく閉じること。
 */

/*
fun saveUserDataToDB(uid: String?, email: String?) {
    if (uid == null || email == null) return

    // --- Firestore例 ---
    // val db = Firebase.firestore
    // val userMap = mapOf(
    //     "email" to email,
    //     "registeredAt" to System.currentTimeMillis()
    // )
    // db.collection("users")
    //     .document(uid)
    //     .set(userMap)
    //     .addOnSuccessListener {
    //         Log.d("Firestore", "User data saved successfully.")
    //     }
    //     .addOnFailureListener {
    //         Log.e("Firestore", "Error saving user data: ${it.message}")
    //     }

    // --- Realtime Database例 ---
    // val rtdb = Firebase.database.reference
    // val userRef = rtdb.child("users").child(uid)
    // userRef.setValue(userMap)
    //     .addOnSuccessListener {
    //         Log.d("RTDB", "User data saved successfully.")
    //     }
    //     .addOnFailureListener {
    //         Log.e("RTDB", "Error saving user data: ${it.message}")
    //     }
}
*/
