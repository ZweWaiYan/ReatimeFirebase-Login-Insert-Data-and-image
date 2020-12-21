package com.letuse.realtimedatabaselogincruddataandimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.letuse.firebasegit.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_email_sign_in.setOnClickListener(this)
        btn_email_create_account.setOnClickListener(this)
        btn_sign_out.setOnClickListener(this)
        btn_test_message.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        when (i) {
            R.id.btn_email_create_account -> createAccount(edtEmail.text.toString(), edtPassword.text.toString())
            R.id.btn_email_sign_in -> signIn(edtEmail.text.toString(), edtPassword.text.toString())
            R.id.btn_sign_out -> signOut()
            R.id.btn_test_message -> testMessage()
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                if (!task.isSuccessful) {
                    tvStatus.text = "Authentication failed!"
                }
            }
    }

    private fun signOut() {
        mAuth!!.signOut()
        updateUI(null)
    }

    private fun testMessage() {
        startActivity(Intent(this, DataActivity::class.java))
    }

    private fun createAccount(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    updateUI(user)

                    //When create new User(Email,password), it save to runtime database
                    writeNewUser(user!!.uid, getUsernameFromEmail(user.email), user.email)
                } else {
                    Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun writeNewUser(userId: String, username: String?, email: String?) {
        val user = User(username, email)
        FirebaseDatabase.getInstance().reference.child("users").child(userId).setValue(user)
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            tvStatus.text = "User Email: " + user.email
            tvDetail.text = "Firebase User ID: " + user.uid

            email_password_buttons.visibility = View.GONE
            email_password_fields.visibility = View.GONE
            layout_signed_in_buttons.visibility = View.VISIBLE
        } else {
            tvStatus.text = "Signed Out"
            tvDetail.text = null

            email_password_buttons.visibility = View.VISIBLE
            email_password_fields.visibility = View.VISIBLE
            layout_signed_in_buttons.visibility = View.GONE
        }
    }
}