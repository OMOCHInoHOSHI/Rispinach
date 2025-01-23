package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


data class ChatMessage(
    val sender: String = "",
    val message: String = "",
    val imageUrl: String? = null
)

class ChatRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("chat")



    fun sendMessage(message: ChatMessage) {
        databaseReference.push().setValue(message)
    }
}

//// firebase realtime databaseにデータを書き込むS-----------
//fun Realtime_Database_Write_Message(){
//    // Write a message to the database
//    val database = Firebase.database
//    val myRef = database.getReference("message")
//
//    myRef.setValue("こんにちは！！！！")
//}
//// firebase realtime databaseにデータを書き込むE-----------


//// Realtime Database でキーだけを取得S--------------------------------------------------------
//fun getNewKeyFromRealtimeDatabase(databaseReference: DatabaseReference): String {
//    val newChildRef = databaseReference.push()
//    val newKey = newChildRef.key ?: ""
//    return newKey
//}
//// Realtime Database でキーだけを取得E--------------------------------------------------------
