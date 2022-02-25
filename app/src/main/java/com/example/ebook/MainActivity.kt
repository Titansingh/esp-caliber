package com.example.ebook

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.ebook.adapters.ViewPagerAdapterbottomnav
import com.example.ebook.daos.BookDao
import com.folioreader.FolioReader
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File

class MainActivity :  FragmentActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var bookDao: BookDao
    lateinit var mAdView : AdView
    private lateinit var viewPager: ViewPager2
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navView : BottomNavigationView
    private lateinit var  readFav : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.bottomNavigationView)
        readFav = findViewById(R.id.fab)
        viewPager = findViewById<ViewPager2>(R.id.fragmentContainerView)


        val pagerAdapter = ViewPagerAdapterbottomnav(supportFragmentManager, lifecycle)
        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPageSelected(position: Int) {
                changepos(position)
            }
        }
        viewPager.registerOnPageChangeCallback(myPageChangeCallback)
        viewPager.adapter = pagerAdapter

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1081601290632-sjmbp696nesaese0b0oevvntu5ff8igk.apps.googleusercontent.com")
            .requestEmail()
            .build()
        auth = Firebase.auth
        bookDao = BookDao()
        bookDao.checkBook()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        isReadStoragePermissionGranted()
        navView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_read->viewPager.currentItem = 0
                R.id.nav_home->viewPager.currentItem = 1

            }
            true
        }
        navView.setOnNavigationItemReselectedListener{
            when(it.itemId){
                R.id.nav_read->viewPager.currentItem = 0
                R.id.nav_home->viewPager.currentItem = 1

            }
            true
        }


        readFav.setOnClickListener{

            when(viewPager.currentItem){
                0-> readFromStorage()
                1-> signOut()
            }

        }




    }
    private fun changepos(position: Int) {
        when (position) {
            0 -> {
                navView.setSelectedItemId(R.id.nav_read)
                readFav.setImageDrawable(getDrawable(R.drawable.folder))

            }

            1 -> {
                navView.setSelectedItemId(R.id.nav_home)

                readFav.setImageDrawable(getDrawable(R.drawable.logout))
            }

        }
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("You cannot access files on our store after signing out!")
        builder.setPositiveButton("Yes") { dialog, which ->
            googleSignInClient.signOut()
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(true)
        builder.show()
    }

    private fun readFromStorage(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ){
            Toast.makeText(this,"Grant Permission First",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        else {
            showFileChooser()
        }

    }
    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"), 1
            )
        } catch (ex: ActivityNotFoundException) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(
                this, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode === AppCompatActivity.RESULT_OK) {
                // Get the Uri of the selected file
                val uri: Uri? = data!!.data
                val uriString =uri!!.path.toString()
                Log.e("filex",uriString)
                val myFile = File(uriString)
                var path = myFile.canonicalPath
                path=path.split(":")[1]
                val folioReader = FolioReader.get()
                val file = File(Environment.getExternalStorageDirectory(), path)
                folioReader.openBook(file.path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("TAG", "Permission is granted")
                true
            } else {
                Log.v("TAG", "Permission is revoked1")
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted1")
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                Log.d("TAG", "External storage1")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0])
                    //resume tasks needing this permission

                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("STORAGE ACCESS")
                    builder.setMessage("You cannot read files from storage without giving permission. Allow storage access?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        isReadStoragePermissionGranted()
                    }
                    builder.setNegativeButton("No") { dialog, which ->
                        Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show()
                    }
                    builder.setCancelable(true)
                    builder.show()
                }
            }
        }
    }




}