package com.letuse.realtimedatabaselogincruddataandimage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private var user: FirebaseUser? = null

    private var dbReference: DatabaseReference? = null

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
        var detail_key: String = messagedetail!!.key

        txt_author_detail.text = detail_author
        txt_time_detail.text = detail_time
        txt_body_detail.text = detail_body

        btn_delete.setOnClickListener {
            dbReference!!.child(user!!.uid).child("Data").child(detail_key).removeValue()
        }

        btn_edit.setOnClickListener {
            var name_changed = edt_body_detail.text.toString()

            dbReference!!.child(user!!.uid).child("Data").child(detail_key).child("body").setValue(name_changed)

            txt_body_detail.text = name_changed
            edt_body_detail.setText("")
        }
    }
}