package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

//import com.google.android.gms.auth.api.signin.GoogleSignIn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

//val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(/*auth: FirebaseAuth*/)
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Google サインインの設定
//    val googleSignInClient = remember {
//        GoogleSignIn.getClient(
//            context,
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("YOUR_CLIENT_ID.apps.googleusercontent.com")  // Firebase Console から取得
//                .requestEmail()
//                .build()
//        )
//    }

    // Google サインイン結果のコールバック
    val googleSignInResult =
        rememberUpdatedState { task: Task<GoogleSignInAccount> ->
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//                    auth.signInWithCredential(credential)
//                        .addOnCompleteListener { authTask ->
//                            if (authTask.isSuccessful) {
//                                Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show()
//                            } else {
//                                Toast.makeText(context, "認証に失敗しました", Toast.LENGTH_SHORT).show()
//                            }
//                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "認証に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }

    // Google サインインの結果を受け取るコールバック
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            //val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            //googleSignInResult.value(task)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Googleログイン") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // メールアドレス入力
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

                // パスワード入力
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

                // ログインボタン
                Button(
                    onClick = {
                        isLoading = true
                        // Firebase Authentication によるメール・パスワードでのログイン処理
//                        auth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener { task ->
//                                isLoading = false
//                                if (task.isSuccessful) {
//                                    Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    Toast.makeText(context, "認証に失敗しました", Toast.LENGTH_SHORT).show()
//                                }
//                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("Login", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google サインインボタン
//                Button(
//                    onClick = {
//                        val signInIntent = googleSignInClient.signInIntent
//                        googleSignInLauncher.launch(signInIntent)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(contentColor = Color.Green/*backgroundColor = Color.Green*/)
//                ) {
//                    Text("Sign in with Google", fontSize = 18.sp, color = Color.White)
//                }

                // ローディングインジケータ
                if (isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}

//fun signInWithGoogle(idToken: String) {
//    val credential = GoogleAuthProvider.getCredential(idToken, null)
//    auth.signInWithCredential(credential)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // サインイン成功
//                val user = auth.currentUser
//                // UI 更新など
//            } else {
//                // サインイン失敗
//            }
//        }
//}