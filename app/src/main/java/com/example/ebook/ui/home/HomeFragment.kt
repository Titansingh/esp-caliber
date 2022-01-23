package com.example.ebook.ui.home


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ebook.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })




        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.openstorage.setOnClickListener(){

            isReadStoragePermissionGranted()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun checkforpermission(){
//        // check permission first
//        if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            // request the permission
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                3)
//        }
//        else {
//            // has the permission.
//            saveFile();
//        }
//
//    }


    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("TAG", "Permission is granted1")
                showFileChooser()
//                val config: Config = Config()
//                    .setAllowedDirection(Config.AllowedDirection.ONLY_VERTICAL)
//                    .setDirection(Config.Direction.VERTICAL)
//                    .setFont(Constants.FONT_LORA)
//                    .setFontSize(5)
//                    .setNightMode(true)
//                    .setShowTts(true)
//
//                val folioReader = FolioReader.get()
//                val path = File(Environment.getExternalStorageDirectory(),file!!)
//                val paths = path.path
//                folioReader.openBook(paths)
                true
            } else {

                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    3)
                Toast.makeText(requireActivity(),"Permission not Granted",Toast.LENGTH_SHORT).show()
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
            2 -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser()

                    //resume tasks needing this permission
//                    val config: Config = Config()
//                        .setAllowedDirection(Config.AllowedDirection.ONLY_VERTICAL)
//                        .setDirection(Config.Direction.VERTICAL)
//                        .setFont(Constants.FONT_LORA)
//                        .setFontSize(5)
//                        .setNightMode(true)
//                        .setShowTts(true)
//
//                    val folioReader = FolioReader.get()
//                    val path = File(Environment.getExternalStorageDirectory(),"qbc.epub")
//                    val paths = path.path
//                    folioReader.openBook(paths)

                } else {
                    Toast.makeText(requireActivity(),"Permission not Granted",Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //resume tasks needing this permission

                } else {

                }
            }
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
                requireContext(), "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}