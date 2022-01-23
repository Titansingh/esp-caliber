package com.example.ebook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.ebook.adapters.IPageAdapter
import com.example.ebook.adapters.ViewPagerAdapter
import me.relex.circleindicator.CircleIndicator3

class OnBoardingActivity : AppCompatActivity(), IPageAdapter {
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
        val adapter = ViewPagerAdapter(instructions, imageUrls, this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val circleIndicator3: CircleIndicator3 = findViewById(R.id.circleIndicator)
        circleIndicator3.setViewPager(viewPager)

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

    override fun onGetStartedClicked() {
        saveData()
        startActivity(Intent(this, SplashScreenActivity::class.java))
    }

}