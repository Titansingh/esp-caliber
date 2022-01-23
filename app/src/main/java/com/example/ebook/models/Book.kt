package com.example.ebook.models

class Book (
    val ownerId: String = User().uid,
    var totalReadTime: Double,
    val bookList : ArrayList<String> = ArrayList()
)