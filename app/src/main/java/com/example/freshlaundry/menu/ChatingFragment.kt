package com.example.freshlaundry.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.adapter.MessageAdapter
import com.example.freshlaundry.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.example.freshlaundry.R

class ChatingFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var dbRef: DatabaseReference
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    private val adminUid = "rqwKfr2gc9bKfE2TFspJ8b7s25o1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chating, container, false)

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageBox = view.findViewById(R.id.messageBox)
        sendButton = view.findViewById(R.id.sendButton)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList, currentUserUid)

        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        chatRecyclerView.adapter = messageAdapter

        dbRef = FirebaseDatabase.getInstance().getReference("chats")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        if ((message?.senderId == currentUserUid && message.receiverId == adminUid) ||
                            (message?.senderId == adminUid && message.receiverId == currentUserUid)
                        ) {
                            if (message != null) {
                                messageList.add(message)
                            }
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString()
            if (messageText.isNotEmpty()) {
                val message = Message(
                    senderId = currentUserUid,
                    receiverId = adminUid,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
                dbRef.push().setValue(message)
                messageBox.setText("")
            }
        }

        return view
    }
}

