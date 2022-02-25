package com.example.ebook

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ebook.adapters.ViewPagerAdapterbottomnav
import com.example.ebook.daos.BookDao
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity :  FragmentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bookDao: BookDao
    lateinit var mAdView : AdView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
//        val navController = findNavController(R.id.fragmentContainerView)
//        navView.setupWithNavController(navController)
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.fragmentContainerView)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ViewPagerAdapterbottomnav(supportFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter


        when (viewPager.currentItem) {
            0 -> {}
            1 -> {}

        }





        navView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_read->viewPager.currentItem = 0
                R.id.nav_home->viewPager.currentItem = 1

            }
            true
        }
        navView.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.nav_read->viewPager.currentItem = 0
                R.id.nav_home->viewPager.currentItem = 1

            }
            true
        }






        auth = Firebase.auth
        bookDao = BookDao()
        bookDao.checkBook()
        isReadStoragePermissionGranted()


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