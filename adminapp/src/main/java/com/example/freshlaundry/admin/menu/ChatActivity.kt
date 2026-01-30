package com.example.freshlaundry.admin.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.adapter.MessageAdapter
import com.example.freshlaundry.admin.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messageList = mutableListOf<Chat>()
    private lateinit var edtMessage: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private val senderId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        receiverId = intent.getStringExtra("userId").orEmpty()
        receiverName = intent.getStringExtra("userEmail").orEmpty().substringBefore("@")

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.chatToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = receiverName
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Tombol back


        recyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        adapter = MessageAdapter(messageList, senderId)
        recyclerView.adapter = adapter

        edtMessage = findViewById(R.id.messageBox)
        btnSend = findViewById(R.id.sendButton)

        btnSend.setOnClickListener {
            val msg = edtMessage.text.toString().trim()
            if (msg.isNotEmpty()) {
                val chat = Chat(senderId, receiverId, msg, System.currentTimeMillis())
                FirebaseDatabase.getInstance().getReference("chats").push().setValue(chat)
                edtMessage.text.clear()
            }
        }

        loadMessages()
    }

    private fun loadMessages() {
        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (snap in snapshot.children) {
                        val chat = snap.getValue(Chat::class.java)
                        if ((chat?.senderId == senderId && chat.receiverId == receiverId) ||
                            (chat?.senderId == receiverId && chat.receiverId == senderId)) {
                            chat?.let { messageList.add(it) }
                        }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
