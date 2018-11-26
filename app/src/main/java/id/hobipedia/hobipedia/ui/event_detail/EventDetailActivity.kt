package id.hobipedia.hobipedia.ui.event_detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_EVENTS
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_ID_EVENT
import id.hobipedia.hobipedia.model.Event
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_event_detail.*

class EventDetailActivity : AppCompatActivity() {

    private var mActionBar: ActionBar? = null
    private var mExtras: Bundle? = null
    private var mEventId: String? = null

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        setupFirebase()

        mActionBar = supportActionBar
        mActionBar?.setDisplayHomeAsUpEnabled(true)

        mExtras = intent?.extras
        mEventId = mExtras?.getString(KEY_ID_EVENT)
        if (mEventId != null) {
            fetchEvent(mEventId!!)
        }
    }


    private fun setupFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth?.currentUser
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase?.reference
    }


    private fun fetchEvent(id: String) {
        mDatabaseReference?.child(CHILD_EVENTS)?.child(id)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val event = p0.getValue(Event::class.java)
                        textViewEventName.text = event?.name
                        textViewAlamat.text = event?.address
                        textViewWaktu.text = event?.time
                        textViewAnggota.text = "10"
                    }

                })
    }
}
