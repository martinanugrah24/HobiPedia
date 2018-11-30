package id.hobipedia.hobipedia.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import id.hobipedia.hobipedia.extension.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lmntrx.android.library.livin.missme.ProgressDialog
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.User
import id.hobipedia.hobipedia.util.Constant
import id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET
import id.hobipedia.hobipedia.util.PreferenceHelper
import id.hobipedia.hobipedia.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserRef: DatabaseReference

    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUserRef = FirebaseDatabase.getInstance().reference.child(Constant.CHILD.CHILD_USERS)
        mAuth = FirebaseAuth.getInstance()

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Silakan Menunggu..")
        mProgressDialog.setCancelable(false)

        buttonRegister.setOnClickListener {
            validateForm()
        }

        textViewLogin.setOnClickListener {
            if (mAuth.currentUser != null)
                FirebaseAuth.getInstance().signOut()
            finish()
        }
    }

    // validate user input
    private fun validateForm() {
        val email = editTextEmail.text.toString().trim()
        val nama = editTextName.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (inputNotEmpty(email, nama, password, confirmPassword)) {
            if (password.equals(confirmPassword)) {
                registerUser(email, nama, password)
            } else {
                toast("Kata sandi tidak cocok")
            }
        } else {
            toast("Mohon input semua data")
        }
    }

    private fun registerUser(email: String, nama: String, password: String) {
        mProgressDialog.show()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("Register berhasil")
                val userId = mAuth.currentUser!!.uid
                val alamat = DEFAULT_NOT_SET
                val photoUrl = DEFAULT_NOT_SET
                val newUser = User(userId, nama, email, alamat, photoUrl)

                // send user data to firebase database
                mUserRef.child(userId).setValue(newUser).addOnCompleteListener {
                    mProgressDialog.dismiss()

                    val prefs = PreferenceHelper.defaultPrefs(this)

                    prefs["nama"] = nama
                    prefs["email"] = email
                    prefs["alamat"] = alamat
                    prefs["photoUrl"] = photoUrl

                    sendEmailVerification()
                    FirebaseAuth.getInstance().signOut()
                }

            } else {
                mProgressDialog.dismiss()
                val errorCode = (task.exception as FirebaseAuthException).errorCode
                when (errorCode) {
                    "ERROR_WEAK_PASSWORD" ->
                        toast("Password harus memiliki minimal 6 karakter")
                    "ERROR_EMAIL_ALREADY_IN_USE" ->
                        toast("Email sudah digunakan oleh akun lain")
                    else ->
                        toast("" + (task.exception as FirebaseAuthException).message)
                }
            }
        }
    }

    private fun sendEmailVerification() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val snackbar = Snackbar
                        .make(constraintLayout, "Email verifikasi dikirim ke ${user.email}", Snackbar.LENGTH_LONG)
                        .setAction("BUKA EMAIL") {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                            startActivity(Intent.createChooser(intent, "Choose email client"))
                        }
                snackbar.show()
            } else {
                Toast.makeText(baseContext,
                        "Gagal mengirim email verifikasi",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputNotEmpty(email: String, nama: String, password: String, confirmPassword: String): Boolean {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(nama) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword))
    }

}
