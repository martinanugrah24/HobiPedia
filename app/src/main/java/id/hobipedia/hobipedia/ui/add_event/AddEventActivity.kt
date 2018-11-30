package id.hobipedia.hobipedia.ui.add_event

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.lmntrx.android.library.livin.missme.ProgressDialog
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.util.Constant
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_EVENTS
import id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_event.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppCompatActivity() {

    private var mActionBar: ActionBar? = null
    private lateinit var mProgressDialog: ProgressDialog
    private var mExtras: Bundle? = null
    private lateinit var alertDialog: AlertDialog

    private var mCategoryName: String? = null

    private var PLACE_PICKER_REQUEST = 0
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mDatePickerDialog: DatePickerDialog? = null
    private var mDate = ""

    private var mTimePickerDialog: TimePickerDialog? = null
    private var mTime = ""

    private var imagePath: String? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        setupFirebase()
        progressBar.visibility = View.GONE
        mActionBar = supportActionBar
        mActionBar?.setDisplayHomeAsUpEnabled(true)
        mExtras = intent.extras
        mCategoryName = mExtras?.getString(Constant.KEY.KEY_NAMA_CATEGORY)
        mActionBar?.title = "Buat Event: $mCategoryName"

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Sedang mengupload..")
        mProgressDialog.setCancelable(false)

        imageViewUploadFoto.setOnClickListener {
            EasyImage.openChooserWithGallery(this, "Pilih Gambar", 0)
        }

        buttonSubmit.setOnClickListener {
            validateForm(mCategoryName!!)
        }

        editTextEventAddress.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            PLACE_PICKER_REQUEST = 1
            showPlacePicker()
        }

        editTextEventDate.setOnClickListener {
            showDateDialog()
        }

        editTextEventTime.setOnClickListener {
            showTimeDialog()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null

    private fun setupFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth?.currentUser
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase?.reference
    }


    private fun validateForm(categoryName: String) {
        val eventId = mDatabaseReference!!.push().key
        val eventName = editTextEventName.text.toString().trim()
        val eventAddress = editTextEventAddress.text.toString().trim()
        val eventDescription = editTextEventDescription.text.toString().trim()
        val eventPhoneNumber = editTextEventPhone.text.toString().trim()
        val eventDate = editTextEventDate.text.toString().trim()
        val eventTime = editTextEventTime.text.toString().trim()
        val eventMaxMember = editTextEventMaxMember.text.toString()
        val eventMinMember = editTextEventMinMember.text.toString()

        if (inputNotEmpty(eventName, eventAddress, eventDescription, eventPhoneNumber, eventDate, eventTime, eventMaxMember, eventMinMember)) {
            uploadEvent(eventAddress, categoryName, eventDescription, eventId, eventName, eventPhoneNumber, eventDate, eventTime, eventMaxMember, eventMinMember)
        } else {
            if (TextUtils.isEmpty(eventName)) {
                textInputLayoutEventName.error = "Mohon masukkan nama event"
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            } else {
                textInputLayoutEventName.error = null
            }
            if (TextUtils.isEmpty(eventAddress)) {
                textInputLayoutEventAddress.error = "Mohon masukkan alamat event"
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            } else {
                textInputLayoutEventAddress.error = null
            }
            if (TextUtils.isEmpty(eventDate)) {
                textInputLayoutEventDate.error = "Mohon masukkan tanggal event"
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            } else {
                textInputLayoutEventDate.error = null
            }
            if (TextUtils.isEmpty(eventTime)) {
                textInputLayoutEventTime.error = "Mohon masukkan waktu event"
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            } else {
                textInputLayoutEventTime.error = null
            }
            if (TextUtils.isEmpty(eventPhoneNumber)) {
                textInputLayoutEventPhone.error = "Mohon masukkan nomor telepon"
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            } else {
                textInputLayoutEventPhone.error = null
            }
            if (TextUtils.isEmpty(eventMaxMember)) {
                editTextEventMaxMember.error = "Masukkan maksimal anggota"
            } else {
                editTextEventMaxMember.error = null
            }
            if (TextUtils.isEmpty(eventMinMember)) {
                editTextEventMinMember.error = "Masukkan minimal anggota"
            } else {
                editTextEventMinMember.error = null
            }
            if (TextUtils.isEmpty(eventDescription)) {
                editTextEventDescription.error = "Mohon masukkan deskripsi event"
            } else {
                editTextEventDescription.error = null
            }
        }
    }


    private fun uploadEvent(eventAddress: String, categoryName: String, eventDescription: String,
                            eventId: String?, eventName: String, eventPhoneNumber: String, eventDate: String,
                            eventTime: String, eventMaxMember: String, eventMinMember: String) {
        showProgressDialog()
        val event = Event(eventAddress, categoryName, eventDescription, eventId, mLatitude, mLongitude, eventName, mFirebaseUser!!.uid, eventPhoneNumber, DEFAULT_NOT_SET, eventDate, eventTime, eventMaxMember.toInt(), eventMinMember.toInt())
        mDatabaseReference?.child(CHILD_EVENTS)?.child(eventId.toString())?.setValue(event)
                ?.addOnSuccessListener {
                    alertDialog.dismiss()
                    toast("Upload berhasil")
                    finish()
                }
                ?.addOnFailureListener {
                    alertDialog.dismiss()
                    toast("Upload gagal")
                }
        uploadFoto(eventId!!)
    }


    private fun showPlacePicker() {
        val builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            progressBar.visibility = View.GONE
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(data!!, this)
                editTextEventAddress.setText(String.format(place.address.toString()))
                mLatitude = place.latLng.latitude
                mLongitude = place.latLng.longitude
            }
        }
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                val bitmap = BitmapFactory.decodeFile(imageFile.toString())
                imageViewUploadFoto.setImageBitmap(bitmap)
                imagePath = imageFile.toString()
                imageUri = Uri.parse(imagePath)
            }

            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                // TODO: error handling
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@AddEventActivity)
                    if (photoFile != null) {
                        photoFile.delete()
                    }
                }
            }
        })
    }


    private fun showDateDialog() {
        val newCalendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        mDatePickerDialog = DatePickerDialog(this, OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            mDate = dateFormatter.format(newDate.time)
            editTextEventDate.setText(mDate)
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH))
        mDatePickerDialog!!.show()
    }


    private fun showTimeDialog() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
        mTimePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
            val newTime = Calendar.getInstance()
            newTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            newTime.set(Calendar.MINUTE, selectedMinute)
            mTime = timeFormatter.format(newTime.time)
            editTextEventTime.setText(mTime)
        }, hour, minute, true)
        mTimePickerDialog!!.setTitle("Select Time")
        mTimePickerDialog!!.show()
    }


    @SuppressLint("InflateParams")
    private fun showProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    private fun uploadFoto(eventId: String?) {
        if (imagePath != null) {
            val imageFile = File(imagePath)

            val compressedImage = Compressor(this)
                    .setMaxWidth(300)
                    .setMaxHeight(300)
                    .setQuality(50)
                    .compressToBitmap(imageFile)

            val baos = ByteArrayOutputStream()
            compressedImage?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()

            val storageReference = FirebaseStorage.getInstance().reference
            val filePath = storageReference.child("images/").child("$eventId.jpg")

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
                    mDatabaseReference?.child(CHILD_EVENTS)?.child(eventId.toString())?.child("photoUrl")?.setValue(downloadUri.toString())
                } else {
                }
            }
        }
    }


    private fun inputNotEmpty(evetName: String, eventAddress: String, eventDescription: String,
                              phoneNumber: String, eventDate: String, eventTime: String,
                              eventMaxMember: String, eventMinMember: String): Boolean {
        return !(TextUtils.isEmpty(evetName)
                || TextUtils.isEmpty(eventAddress) || TextUtils.isEmpty(eventDescription)
                || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(eventDate)
                || TextUtils.isEmpty(eventTime) || TextUtils.isEmpty(eventMaxMember)
                || TextUtils.isEmpty(eventMinMember))
    }
}
