package com.letuse.realtimedatabaselogincruddataandimage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.letuse.firebasegit.model.Message
import com.letuse.firebasegit.model.User
import kotlinx.android.synthetic.main.fragment_data.*
import java.text.SimpleDateFormat
import java.util.*

class DataFragment : Fragment() {
    private var user: FirebaseUser? = null

    private var dbReference: DatabaseReference? = null

    private var mAdapter: FirebaseRecyclerAdapter<Message, UserHolder>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbReference = FirebaseDatabase.getInstance().getReference("users")
        user = FirebaseAuth.getInstance().currentUser

        btn_send.setOnClickListener {
            var body = edt_body.text.toString()

            createUser(body)

            edt_body.setText("")
        }

        val query = dbReference!!.child(user!!.uid).child("Data").limitToLast(8)

        val childEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(User::class.java)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(User::class.java)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val message = dataSnapshot.getValue(User::class.java)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        query.addChildEventListener(childEventListener)

        val options = FirebaseRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java)
                .setLifecycleOwner(this).build()


        mAdapter = object : FirebaseRecyclerAdapter<Message, UserHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
                return UserHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_next, parent, false)
                )
            }

            override fun onBindViewHolder(holder: UserHolder, position: Int, model: Message) {
                holder.bind(model)

                holder.itemView.setOnClickListener {
                    findNavController().navigate(
                        DataFragmentDirections.actionDataFragmentToDetailFragment(
                            model.author.toString(),
                            model.body.toString(),
                            model.time.toString(),
                            model.key.toString()
                        )
                    )
                }
            }
        }

        //
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        layoutManager.reverseLayout = false
        recyclerview_user.setHasFixedSize(true)
        recyclerview_user.layoutManager = layoutManager
        recyclerview_user.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }

    private fun createUser(body: String) {

        var key = dbReference!!.child(user!!.uid).child("Data").push().key.toString()

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        val message = Message(getUsernameFromEmail(user!!.email), body, time, key)

        dbReference!!.child(user!!.uid).child("Data").child(key).setValue(message)
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
}