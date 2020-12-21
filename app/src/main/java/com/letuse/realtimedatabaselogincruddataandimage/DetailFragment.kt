package com.letuse.realtimedatabaselogincruddataandimage

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.item_next.view.*
import java.util.*

class DetailFragment : Fragment() {

    private var user: FirebaseUser? = null
    private var mProgress: ProgressDialog? = null
    private var dbReference: DatabaseReference? = null
    var selectedPhotoUri : Uri? = null
    var detail_key: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbReference = FirebaseDatabase.getInstance().getReference("users")
        user = FirebaseAuth.getInstance().currentUser

        var messagedetail = arguments?.let { DetailFragmentArgs.fromBundle(it) }
        var detail_author: String = messagedetail!!.author
        var detail_body: String = messagedetail!!.body
        var detail_time: String = messagedetail!!.time
        detail_key = messagedetail!!.key
        var detail_img: String = messagedetail!!.imgUrl

        txt_author_detail.text = detail_author
        txt_time_detail.text = detail_time
        txt_body_detail.text = detail_body
        Picasso.get().load(detail_img).into(view_edit_img);

        btn_edit.setOnClickListener {
            mProgress = ProgressDialog(this.requireContext())
            mProgress!!.setMessage("Uploading to Server....")
            mProgress!!.show()
            if (selectedPhotoUri==null){
                saveUserToFirebaseDatabase(detail_img!!,detail_key)
            }else{
                uploadImageToFirebaseStorage(detail_key)
            }
        }

        btn_edit_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

        btn_delete.setOnClickListener {
            dbReference!!.child(user!!.uid).child("Data").child(detail_key).removeValue()
            findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToDataFragment2())
        }

    }

    private fun uploadImageToFirebaseStorage(fetailKey: String) {
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
                    saveUserToFirebaseDatabase(it.toString(),fetailKey)
                }
            }
    }

    private fun saveUserToFirebaseDatabase(iteImageUrl: String, index: String) {
        var name_changed = edt_body_detail.text.toString()

        dbReference!!.child(user!!.uid).child("Data").child(detail_key).child("itemImageUrl").setValue(iteImageUrl)
        dbReference!!.child(user!!.uid).child("Data").child(detail_key).child("body").setValue(name_changed)

        txt_body_detail.text = name_changed

        Picasso.get().load(iteImageUrl).into(view_edit_img);
        edt_body_detail.setText("")

        mProgress!!.dismiss()
        findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToDataFragment2())
    }

    //////////////////////////////////////////////Select Image and Convert bitmap
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            view_edit_img.setImageDrawable(bitmapDrawable)
        }
    }
}