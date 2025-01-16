package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
import androidx.navigation.NavController



/**
 * ログイン画面。
 * - メール＆パスワードでのログイン/新規登録
 * - Googleログイン対応
 * - 成功したら onLoginSuccess() または onSignUpSuccess() を呼ぶ
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,      // ← ここを追加
    onLoginSuccess: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
) {
    FirebaseApp.initializeApp(LocalContext.current)

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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

    // Googleログイン結果
    val googleSignInResult = rememberUpdatedState { task: Task<GoogleSignInAccount> ->
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(context, "Googleログイン成功", Toast.LENGTH_SHORT).show()
                        // HomeScreenへ遷移する場合:
                        navController.navigate("HomeScreen") {
                            popUpTo("LoginScreen") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Google認証に失敗しました", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google認証に失敗: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Googleサインインの結果を受け取るLauncher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            googleSignInResult.value(task)
        }
    )

    // ログイン処理
    fun login() {
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show()
                    navController.navigate("HomeScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "ログイン失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 新規登録処理
    fun signUp() {
        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "ユーザー登録成功", Toast.LENGTH_SHORT).show()
                    navController.navigate("HomeScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                    onSignUpSuccess()
                } else {
                    Toast.makeText(context, "登録失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Firebase Authentication") })
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
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

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

                // ログイン
                Button(
                    onClick = { login() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("Login (既存ユーザー)", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 新規登録
                Button(
                    onClick = { signUp() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    Text("SignUp (新規登録)", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Googleログイン
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
