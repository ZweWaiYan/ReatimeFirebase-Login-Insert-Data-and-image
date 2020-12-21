package com.letuse.realtimedatabaselogincruddataandimage

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.letuse.firebasegit.model.Message
import kotlinx.android.synthetic.main.fragment_add_data.*
import kotlinx.android.synthetic.main.fragment_data.*
import java.text.SimpleDateFormat
import java.util.*

class AddDataFragment : Fragment() {

    private var user: FirebaseUser? = null
    private var mProgress: ProgressDialog? = null
    private var dbReference: DatabaseReference? = null

    var selectedPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbReference = FirebaseDatabase.getInstance().getReference("users")
        user = FirebaseAuth.getInstance().currentUser
        mProgress = ProgressDialog(this.requireContext())

        btn_tosend.setOnClickListener {
            if (edt_body.text.isNotEmpty()) {
                uploadImageToFirebaseStorage()
            }
        }

        btn_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    //////////////////////////////////////////////Select Image and Convert bitmap
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(scaleBitMap(bitmap))
            view_img.setImageDrawable(bitmapDrawable)
        }
    }

    private fun scaleBitMap(bm: Bitmap): Bitmap {
        var width: Int = bm.width
        var height: Int = bm.height

        var bitmap = bm

        Log.d("Pictures", "Width and height are " + width + "--" + height);

        height = 600
        width = 600

        Log.d("Pictures", "after scaling Width and height are " + width + "--" + height)

        bitmap = Bitmap.createScaledBitmap(bm, width, height, true);

        return bitmap
    }

    //////////////////////////////////////////////Upload Image to firebase store
    private fun uploadImageToFirebaseStorage() {
        mProgress = ProgressDialog(this.requireContext())
        mProgress!!.setMessage("Uploading to Server....")
        mProgress!!.show()
        if (selectedPhotoUri == null) return
        // random filename
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("InsertFragment>>", "Successfully upload image : ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("InsertFragment>>", "File location : $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    //////////////////////////////////////////////Insert to Firebase
    private fun saveUserToFirebaseDatabase(iteImageUrl: String) {
        var body = edt_body.text.toString()

        var key = dbReference!!.child(user!!.uid).child("Data").push().key.toString()

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        val message = Message(getUsernameFromEmail(user!!.email), body, time, key , iteImageUrl)

        dbReference!!.child(user!!.uid).child("Data").child(key).setValue(message)

        mProgress!!.dismiss()
        edt_body.setText("")

        findNavController().navigate(AddDataFragmentDirections.actionAddDataFragmentToDataFragment())
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
}