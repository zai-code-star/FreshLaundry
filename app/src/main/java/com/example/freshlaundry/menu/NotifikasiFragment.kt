package com.example.freshlaundry.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.LoginActivity
import com.example.freshlaundry.R
import com.example.freshlaundry.adapter.NotifikasiAdapter
import com.example.freshlaundry.model.Notifikasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotifikasiFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Penting supaya menu bisa tampil di fragment
    }
    private lateinit var recyclerView: RecyclerView
    private val listNotifikasi = mutableListOf<Notifikasi>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifikasi, container, false)
    }

    // Set Toolbar sebagai ActionBar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewNotifikasi)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar_notifikasi)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Notifikasi" // <- Tambahkan baris ini

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance().getReference("Notifikasi").child(userId)
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listNotifikasi.clear()
                    for (data in snapshot.children) {
                        val notif = data.getValue(Notifikasi::class.java)
                        notif?.let { listNotifikasi.add(it) }
                    }
                    recyclerView.adapter = NotifikasiAdapter(listNotifikasi.reversed()) // Tampilkan yang terbaru di atas
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Inflate menu logout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    // Aksi saat logout ditekan
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
