package id.hobipedia.hobipedia.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import id.hobipedia.hobipedia.extension.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lmntrx.android.library.livin.missme.ProgressDialog
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.User
import id.hobipedia.hobipedia.util.PreferenceHelper
import id.hobipedia.hobipedia.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_login.*
import id.hobipedia.hobipedia.util.Constant


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Silakan menunggu...")
        mProgressDialog.setCancelable(false)

        buttonLogin.setOnClickListener {
            loginUser()
        }

        textViewDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressDialog.show()
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        mProgressDialog.dismiss()
                        if (task.isSuccessful) {
                            toast("Login berhasil")
                            getDataUser()
                        } else {
                            val errorCode = (task.exception as FirebaseAuthException).errorCode
                            when (errorCode) {
                                "ERROR_WRONG_PASSWORD" ->
                                    toast("Email/Password Anda salah")
                                "ERROR_USER_NOT_FOUND" ->
                                    toast("Akun belum terdaftar")
                                else ->
                                    toast("" + (task.exception as FirebaseAuthException).message)
                            }
                        }
                    }
        } else if (email.isEmpty() && password.isEmpty()) {
            toast("Mohon masukkan email dan password")
        } else if (email.isEmpty()) {
            toast("Mohon masukkan email")
        } else if (password.isEmpty()) {
            toast("Mohon masukkan password")
        }
    }

    private fun getDataUser() {
        val userRef = FirebaseDatabase.getInstance().reference.child(Constant.CHILD.CHILD_USERS)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userRef.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                val nama = user?.nama
                val email = user?.email
                val alamat = user?.alamat

                val prefs = PreferenceHelper.defaultPrefs(this@LoginActivity)

                prefs["nama"] = nama
                prefs["email"] = email
                prefs["alamat"] = alamat

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        currentUser?.let {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}
