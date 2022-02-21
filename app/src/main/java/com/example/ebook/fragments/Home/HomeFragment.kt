package com.example.ebook.fragments.Home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ebook.AddMob
import com.example.ebook.MainActivity
import com.example.ebook.R
import com.example.ebook.SignInActivity
import com.example.ebook.adapters.BooksReadRVAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private lateinit var mAdapter: BooksReadRVAdapter

    private lateinit var viewModel: HomeViewModel
    private val db = FirebaseFirestore.getInstance()
    private val profileCollection = db.collection("profiles")
    private val userCollection = db.collection("users")
    private val auth = Firebase.auth
    lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1081601290632-sjmbp696nesaese0b0oevvntu5ff8igk.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val name: TextView = view.findViewById(R.id.textViewUserName)
        val email: TextView = view.findViewById(R.id.textViewUserEmail)
        val userReadTime: TextView = view.findViewById(R.id.textViewUserReadTime)
        val userImage: ImageView = view.findViewById(R.id.imageViewUser)
        val signOutButton: Button = view.findViewById(R.id.signOutButton)
        val currentUser = auth.currentUser!!.uid
        var currentReadTime: Double
        var userName: String
        var userEmail: String
        var imageUrl: String
        var booksReadList: ArrayList<String>
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBooksRead)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        signOutButton.setOnClickListener {
           signout()


//            val mainActivityIntent = Intent(requireActivity(), AddMob::class.java)
//            startActivity(mainActivityIntent)



        }

        val query: Task<QuerySnapshot> =
            profileCollection.whereEqualTo("ownerId", currentUser).get()
        query.addOnSuccessListener {
            currentReadTime = it.documents[0]["totalReadTime"].toString().toDouble()
            booksReadList = it.documents[0]["bookList"] as ArrayList<String>
            userReadTime.text = "Total Read time: ${currentReadTime.roundToInt()} minutes"
            mAdapter = BooksReadRVAdapter(requireContext(),booksReadList)
            recyclerView.adapter = mAdapter
            recyclerView.itemAnimator = null
        }

        val query1: Task<QuerySnapshot> =
            userCollection.whereEqualTo("uid", currentUser).get()
        query1.addOnSuccessListener {
            userName = it.documents[0]["displayName"].toString()
            userEmail = it.documents[0]["email"].toString()
            imageUrl = it.documents[0]["imageUrl"].toString()

            name.text = userName
            email.text = userEmail
            Glide.with(this).load(imageUrl).circleCrop().into(userImage)
        }




    }
    fun signout(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure?")
        builder.setMessage("You cannot access files on our store after signing out!")
        builder.setPositiveButton("Yes") { dialog, which ->
            googleSignInClient.signOut()
            auth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(requireContext(), "Operation Canceled", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(true)
        builder.show()
    }

}