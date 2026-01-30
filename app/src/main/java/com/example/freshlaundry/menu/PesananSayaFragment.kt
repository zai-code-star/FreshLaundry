package com.example.freshlaundry.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.R
import com.example.freshlaundry.adapter.PesananAdapter
import com.example.freshlaundry.model.Pesanan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PesananSayaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananList: ArrayList<Pesanan>
    private lateinit var adapter: PesananAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pesanan_saya, container, false)
        recyclerView = view.findViewById(R.id.rvPesanan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        pesananList = arrayListOf()
        adapter = PesananAdapter(pesananList)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: ""

        databaseRef = FirebaseDatabase.getInstance().getReference("Orders")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pesananList.clear()
                for (tanggalSnapshot in snapshot.children) {
                    for (jamSnapshot in tanggalSnapshot.children) {
                        for (orderSnapshot in jamSnapshot.children) {
                            val pesanan = orderSnapshot.getValue(Pesanan::class.java)
                            pesanan?.let {
                                it.key = orderSnapshot.key // <-- Simpan key node Firebase
                                if (it.uid == uid) {
                                    pesananList.add(it)
                                }
                            }
                        }
                        }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return view
    }
}
