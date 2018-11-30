package id.hobipedia.hobipedia.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.User
import id.hobipedia.hobipedia.ui.LoginActivity
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_USERS
import id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_profile.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileFragment : Fragment() {

    private var profilChanged = false

    lateinit var alertDialog: AlertDialog

    private var imagePath: String? = null
    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_container?.let { it.visibility = View.GONE }

        keluarBtn.setOnClickListener {
            context?.toast("Logout")
            FirebaseAuth.getInstance().signOut()
            navigateToLoginActivity()
        }

        image_profile.setOnClickListener {
            if (profilChanged)
                EasyImage.openChooserWithGallery(this, "Pilih Gambar", 0)
        }

        val userRef = FirebaseDatabase.getInstance().reference.child(CHILD_USERS)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userRef.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                progressBar.visibility = View.GONE
                profile_container?.let { it.visibility = View.VISIBLE }
                val user = p0.getValue(User::class.java)
                val nama = user?.nama
                val email = user?.email
                val alamat = user?.alamat
                val photoUrl = user?.photoUrl!!

                nama_lengkap_et.setText(nama)
                email_et.setText(email)
                alamat_et.setText(alamat)

                if (photoUrl != DEFAULT_NOT_SET) {
                    Glide.with(context!!)
                            .load(photoUrl)
                            .into(image_profile)
                } else {
                    image_profile.setBackgroundResource(R.drawable.ic_avatar)
                }

            }
        })

        ubahBtn.setOnClickListener {
            if (!profilChanged) {
                profilChanged = true
                ubahBtn.text = "Simpan"
                nama_lengkap_et.isEnabled = true
                email_et.isEnabled = true
                alamat_et.isEnabled = true
                nama_lengkap_et.requestFocus()
            } else {
                profilChanged = false
                ubahBtn.text = "Ubah"
                saveProfile()
            }

        }
    }

    private fun saveProfile() {
        showProgressDialog()
        if (imageUri != null) {
            uploadFoto()
        } else {
            sendUserProfileToFirebase()
        }
    }

    private fun sendUserProfileToFirebase() {
        val nama = nama_lengkap_et.text.toString().trim()
        val email = email_et.text.toString().trim()
        val alamat = alamat_et.text.toString().trim()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val mRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
        val newUserProfile = User(userId, nama, email, alamat, DEFAULT_NOT_SET)

        mRef.setValue(newUserProfile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                alertDialog.dismiss()
                Toast.makeText(context, "Profile berhasil diubah", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        nama_lengkap_et.isEnabled = false
        email_et.isEnabled = false
        alamat_et.isEnabled = false
    }

    private fun uploadFoto() {
        if (imagePath != null) {
            val imageFile = File(imagePath)

            val compressedImage = Compressor(context)
                    .setMaxWidth(300)
                    .setMaxHeight(300)
                    .setQuality(50)
                    .compressToBitmap(imageFile)

            val baos = ByteArrayOutputStream()
            compressedImage?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()

            val storageReference = FirebaseStorage.getInstance().reference
            val filePath = storageReference.child("profiles/").child("${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

            val uploadTask = filePath.putBytes(data)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    sendUserProfileToFirebase()
                    FirebaseDatabase.getInstance().reference.child(CHILD_USERS).child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("photoUrl").setValue(downloadUri.toString())
                } else {
                    alertDialog.dismiss()
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(requestCode, resultCode, data, activity, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                val bitmap = BitmapFactory.decodeFile(imageFile.toString())
                image_profile.setImageBitmap(bitmap)
                imagePath = imageFile.toString()
                imageUri = Uri.parse(imagePath)
            }

            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                // TODO: error handling
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(context)
                    if (photoFile != null) {
                        photoFile.delete()
                    }
                }
            }
        })
    }

}
