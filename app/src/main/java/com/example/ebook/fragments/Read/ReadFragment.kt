package com.example.ebook.fragments.Read

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ebook.MainActivity
import com.example.ebook.daos.BookDao
import com.example.ebook.R
import com.example.ebook.adapters.BookListRVAdapter
import com.example.ebook.adapters.IBookListRVAdapter
import com.example.ebook.daos.BookDetailsDao
import com.example.ebook.models.BookDetails
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.folioreader.FolioReader
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ReadFragment : Fragment(), IBookListRVAdapter {

    private lateinit var viewModel: ReadViewModel
    private lateinit var mAdapter : BookListRVAdapter
    private lateinit var bookDetailsDao: BookDetailsDao
    private lateinit var bookDao: BookDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.read_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ReadViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookDetailsDao = BookDetailsDao()
        bookDao = BookDao()
        val bookCollection = bookDetailsDao.bookCollection.orderBy("name",Query.Direction.ASCENDING)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar2)
        progressBar.visibility = View.GONE
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<BookDetails>()
            .setQuery(bookCollection, BookDetails::class.java).build()
        val recyclerView: RecyclerView = view.findViewById(R.id.bookListRecyclerView)
        mAdapter = BookListRVAdapter(recyclerViewOptions, this)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = null

        //for reading from external storage
//        val readButton: Button = view.findViewById(R.id.buttonReadOffline)

//        readButton.setOnClickListener {
//
//            if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//            ){
//                Toast.makeText(requireContext(),"Grant Permission First",Toast.LENGTH_SHORT).show()
//                val intent = Intent(requireContext(),MainActivity::class.java)
//                startActivity(intent)
//            }
//
//            else {
//                showFileChooser()
//            }
//        }

    }

    override fun onBookNameClicked(bookId: String) {
        bookDetailsDao.getBookById(bookId).addOnSuccessListener {
            val bookName = it["name"] as String
            val progressBar: ProgressBar = requireView().findViewById(R.id.progressBar2)
            progressBar.visibility = View.VISIBLE
            val storageRef = FirebaseStorage.getInstance().reference.child("$bookName.epub")

            val localFile = File.createTempFile("temp",".epub")
            storageRef.getFile(localFile).addOnSuccessListener {

                bookDao.addBook(bookName)
                progressBar.visibility = View.GONE
                val folioReader = FolioReader.get()
                folioReader.openBook(localFile.canonicalPath)

            }.addOnFailureListener{
                Toast.makeText(requireContext(),"Failed to fetch eBook",Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

//    private fun showFileChooser() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        try {
//            startActivityForResult(
//                Intent.createChooser(intent, "Select a File to Upload"), 1
//            )
//        } catch (ex: ActivityNotFoundException) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(
//                requireContext(), "Please install a File Manager.",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

//    @SuppressLint("Range")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            1 -> if (resultCode === AppCompatActivity.RESULT_OK) {
//                // Get the Uri of the selected file
//                val uri: Uri? = data!!.data
//                val uriString =uri!!.path.toString()
//                Log.e("filex",uriString)
//                val myFile = File(uriString)
//                var path = myFile.canonicalPath
//                path=path.split(":")[1]
//                val folioReader = FolioReader.get()
//                val file = File(Environment.getExternalStorageDirectory(), path)
//                folioReader.openBook(file.path)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    override fun onStart() {

        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

}