package com.example.ebook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2

import com.example.ebook.adapters.ViewPagerAdapter
import com.example.ebook.adapters.ViewPagerAdapterbottomnav
import me.relex.circleindicator.CircleIndicator3

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var progressBar : ProgressBar
    private lateinit var nextButton : FrameLayout
    private lateinit var nextText : TextView
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val instructions = listOf(
            "Hi, Thanks for installing ePUB eBook reader, now, let's take a quick tour of the app. You need" +
                    " to sign in in order to continue",
            "You can choose one of the books from our storage or read one from your own storage",
            "Click on the icon at top right corner to change text size or font",
            "Long press any text and drag over screen to select multiple lines to highlight, then go over to highlight" +
                    " section to view them later"
        )
        val imageUrls = listOf(
            "https://i.postimg.cc/VvmT2vph/1.jpg",
            "https://i.postimg.cc/76Mt1Dhn/2.jpg",
            "https://i.postimg.cc/FzpqvmQH/3.jpg",
            "https://i.postimg.cc/3NtL6Bgy/4.jpg"
        )
        val adapter = ViewPagerAdapter(instructions, imageUrls)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        nextButton = findViewById(R.id.nextButton)
        nextText = findViewById(R.id.nextText)

        nextButton.setOnClickListener {

            when(viewPager.currentItem) {
                0-> viewPager.setCurrentItem(1)
                1-> viewPager.setCurrentItem(2)
                2-> viewPager.setCurrentItem(3)
                3-> {
                    saveData()
                    startActivity(Intent(this, SplashScreenActivity::class.java))
                    }

            }
            }


        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPageSelected(position: Int) {
                when(position){
                    0->{
                        increseprogress(25)
                        nextText.setText("NEXT")
                    }
                    1->{
                        increseprogress(50)
                        nextText.setText("NEXT")
                    }
                    2->{
                        increseprogress(75)
                        nextText.setText("NEXT")
                    }
                    3->{
                        increseprogress(100)
                        nextText.setText("GET STARTED")
                    }

                }

            }
        }
        viewPager.registerOnPageChangeCallback(myPageChangeCallback)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        progressBar= findViewById(R.id.progress_bar)





    }

    @SuppressLint("ApplySharedPref")
    private fun saveData() {

        val sharedPreferences: SharedPreferences = getSharedPreferences("OnBoardingBoolean",
            Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putBoolean("FIRST_RUN_OVER", true)
        }.commit()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private  fun increseprogress(pos:Int) {
        progressBar.setProgress(pos, true)


    }

}