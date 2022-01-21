package com.example.ebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ebook.databinding.ActivityMainBinding
import com.google.firebase.firestore.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: rc_main_adapter
    private lateinit var bookArrayList: ArrayList<Book>
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        recyclerView = binding.rcMain

        recyclerView.layoutManager=  LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        bookArrayList = arrayListOf()

        myAdapter = rc_main_adapter(bookArrayList)
        recyclerView.adapter = myAdapter

        EventChangeListner()










    }

    private fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("books")
            .addSnapshotListener(object: EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("firestore error",error.message.toString())
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            bookArrayList.add(dc.document.toObject(Book::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }

            })
    }

}