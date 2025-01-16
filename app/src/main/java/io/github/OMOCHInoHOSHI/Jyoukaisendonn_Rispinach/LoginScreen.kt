package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen() {
    // Firebase の初期化 (すでにどこかで呼ばれていれば不要)
    FirebaseApp.initializeApp(LocalContext.current)

    // FirebaseAuth インスタンス
    val auth = remember { FirebaseAuth.getInstance() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // GoogleSignInClient
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1:796232278012:web:e07084ff3d13d8e4a6dc79")
                .requestEmail()
                .build()
        )
    }

    // Googleサインイン結果処理
    val googleSignInResult = rememberUpdatedState { task: Task<GoogleSignInAccount> ->
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Googleログイン成功", Toast.LENGTH_SHORT).show()
                            // 画面遷移やDB登録など
                            // val user = auth.currentUser
                        } else {
                            Toast.makeText(context, "Google認証に失敗しました", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google認証に失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // GoogleサインインIntentの結果受け取り
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            googleSignInResult.value(task)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Firebase Authentication") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Email入力
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password入力
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ログインボタン (既存ユーザー用)
                Button(
                    onClick = {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show()
                                    // val user = auth.currentUser
                                } else {
                                    Toast.makeText(context, "ログイン失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("Login (既存ユーザー)", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // サインアップボタン (新規ユーザー登録)
                Button(
                    onClick = {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "ユーザー登録成功", Toast.LENGTH_SHORT).show()
                                    // val newUser = auth.currentUser
                                } else {
                                    Toast.makeText(context, "登録失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("SignUp (新規登録)", fontSize = 18.sp)
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
                    Text("Sign in with Google", fontSize = 18.sp, color = Color.White)
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    )
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
