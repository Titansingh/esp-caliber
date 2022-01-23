package com.example.ebook.daos

import com.example.ebook.models.Book
import com.example.ebook.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookDao {
    private val db = FirebaseFirestore.getInstance()
    private val profileCollection = db.collection("profiles")
    private val auth = Firebase.auth

    fun checkBook() {

        var contains = false
        val currentUser = auth.currentUser!!.uid
        val query: Task<QuerySnapshot> = profileCollection.whereEqualTo("ownerId", currentUser).get()
        query.addOnSuccessListener {
            if (!(it.isEmpty)) {
                contains = true
            }
            if (!contains) {
                addBookProfile()
            }
        }
    }

    fun getBookById(bookId: String): Task<DocumentSnapshot>{
        return profileCollection.document(bookId).get()
    }

    private fun addBookProfile() {
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUser).await()
                .toObject(User::class.java)!!
            val book = Book(user.uid, 0.0)
            profileCollection.document().set(book)
        }

    }

    fun addBook(bookAdd: String) {
        GlobalScope.launch {

            val currentUser = auth.currentUser!!.uid
            var currentBook = ""
            var currentBookReadTime: Double
            var bookList: ArrayList<String>
            var book: Book
            val query: Task<QuerySnapshot> =
                profileCollection.whereEqualTo("ownerId", currentUser).get()
            query.addOnSuccessListener {
                currentBook = it.documents[0].id
                currentBookReadTime = it.documents[0]["totalReadTime"].toString().toDouble()
                bookList = it.documents[0]["bookList"] as ArrayList<String>
                if(!(bookList.contains(bookAdd))){
                    bookList.add(bookAdd)
                    book = Book(currentUser,currentBookReadTime,bookList)
                    profileCollection.document(currentBook).set(book)
                }

            }
        }

    }

    fun increaseTotalReadTime(time: Double) {
        GlobalScope.launch {

            val currentUser = auth.currentUser!!.uid
            var currentBook = ""
            var currentBookReadTime: Double
            var book: Book
            val query: Task<QuerySnapshot> =
                profileCollection.whereEqualTo("ownerId", currentUser).get()
            query.addOnSuccessListener {
                currentBook = it.documents[0].id
                currentBookReadTime = it.documents[0]["totalReadTime"].toString().toDouble()
                book = Book(currentUser,currentBookReadTime+time)
                profileCollection.document(currentBook).set(book)

            }
        }

    }

}