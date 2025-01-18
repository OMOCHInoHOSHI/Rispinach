package io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach

import com.google.firebase.database.FirebaseDatabase
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

fun Realtime_Database_Write_Message(){
    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("message")

    myRef.setValue("こんにちは！！！！")
}