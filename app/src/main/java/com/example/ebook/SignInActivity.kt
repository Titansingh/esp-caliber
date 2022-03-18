package com.example.ebook

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.example.ebook.MainActivity
import com.example.ebook.adapters.ViewPagerAdapter
import com.example.ebook.adapters.ViewPagerAdapterSignIn
import com.example.ebook.adapters.ViewPagerAdapterbottomnav
import com.example.ebook.models.User
import com.example.ebook.daos.UserDao
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        //auth.createUserWithEmailAndPassword()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id1))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        val signInButton: Button = findViewById(R.id.signInButton)
        val logInButton : Button = findViewById(R.id.logInbutton)
        val emailid: EditText = findViewById(R.id.email)
        val password : EditText = findViewById(R.id.password)
        val tabLayout = findViewById<TabLayout>(R.id.tablayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.viewPagerl)
        val adapter = ViewPagerAdapterSignIn(supportFragmentManager, lifecycle)

        // The pager adapter, which provides the pages to the view pager widget.

        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPageSelected(position: Int) {

            }
        }

        viewPager2.registerOnPageChangeCallback(myPageChangeCallback)

//        viewPager2.setUserInputEnabled(false);
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "login"
                1 -> tab.text = "Signup"

            }
        }.attach()

        signInButton.setOnClickListener {
            signIn()
        }

        logInButton.setOnClickListener {
            if(!isEmpty(emailid.text)&&!isEmpty(password.text)){

            singnInemail(emailid.text.toString(),password.text.toString())}

        }






    }
    private  fun singnInemail(email:String ,password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener(this){task ->
            createUser(email,password)


            }
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private  fun createUser(email: String,password: String){
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.createUserWithEmailAndPassword(email,password).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }


    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d("UserSignInSuccess", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("UserSignInFailed", "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        val signInButton: Button = findViewById(R.id.signInButton)
        signInButton.visibility = View.GONE

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {

            val user = User(currentUser.uid, currentUser.displayName, currentUser.photoUrl.toString(),currentUser.phoneNumber,currentUser.email)
            val usersDao = UserDao()
            usersDao.addUser(user)

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
            val signInButton: Button = findViewById(R.id.signInButton)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

}