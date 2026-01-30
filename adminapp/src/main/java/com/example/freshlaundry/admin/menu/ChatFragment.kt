package com.example.freshlaundry.admin.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.adapter.ChatListAdapter
import com.example.freshlaundry.admin.model.Chat
import com.example.freshlaundry.admin.model.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatListAdapter
    private val userList = mutableListOf<User>()
    private val adminUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recyclerChatList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatListAdapter(userList) { user ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("userId", user.uid)
            intent.putExtra("userEmail", user.email)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        loadChatUsers()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.chatToolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Chat"
    }

    private fun loadChatUsers() {
        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastMessages = mutableMapOf<String, String>()

                    for (snap in snapshot.children) {
                        val chat = snap.getValue(Chat::class.java)
                        if (chat != null && chat.receiverId == adminUid) {
                            lastMessages[chat.senderId] = chat.message
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("users")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnap: DataSnapshot) {
                                userList.clear()
                                for (child in userSnap.children) {
                                    val user = child.getValue(User::class.java)
                                    if (user != null && lastMessages.containsKey(user.uid)) {
                                        user.lastChat = lastMessages[user.uid] ?: ""
                                        userList.add(user)
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}