package id.hobipedia.hobipedia.ui.event_detail

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.model.User
import id.hobipedia.hobipedia.ui.chat.ChatActivity
import id.hobipedia.hobipedia.util.Constant
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_EVENTS
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_USERS
import id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_ID_EVENT
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_NAMA_EVENT
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.util.HashMap

class EventDetailActivity : AppCompatActivity() {

    private var mExtras: Bundle? = null
    private var mEventId: String? = null
    private var mEventName: String? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        mExtras = intent?.extras
        mEventName = mExtras?.getString(KEY_NAMA_EVENT)
        collapse_toolbar.title = mEventName
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        setupFirebase()
        mEventId = mExtras?.getString(KEY_ID_EVENT)
        if (mExtras?.getBoolean("is_join")!!) {
            buttonSubmit.visibility = View.GONE
        }
        if (mEventId != null) {
            fetchEvent(mEventId!!)
            mapFragment.getMapAsync {
                mMap = it
                val lat = mExtras?.getDouble("lat")
                val lng = mExtras?.getDouble("lng")
                val location = LatLng(lat!!, lng!!)
                mMap.addMarker(MarkerOptions().position(location).title("Marker in Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
            buttonSubmit.setOnClickListener {
                var count: Int
                val map = HashMap<String, Any?>()
                mDatabaseReference?.child(CHILD_EVENTS)?.child(mEventId!!)?.child("members")?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        count = (p0.childrenCount - 1).toInt().inc()
                        map.put(count.toString(), mFirebaseAuth?.currentUser?.uid)
                        mDatabaseReference?.child(CHILD_EVENTS)?.child(mEventId!!)?.child("members")?.updateChildren(map)
                    }

                })
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(KEY_ID_EVENT, mEventId!!)
                intent.putExtra(KEY_NAMA_EVENT, textViewEventName.text)
                intent.putExtra("lat", mExtras?.getDouble("lat"))
                intent.putExtra("lng", mExtras?.getDouble("lng"))
                startActivity(intent)
                toast("Berhasil join")
                finish()
            }
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
                        textViewEventCategory.text = event?.category
                        fetchOwnerEvent(event?.ownerId!!)
                        textViewEventDate.text = event.date
                        textViewEventTime.text = event.time
                        textViewEventMinMember.text = event.minMember.toString()
                        textViewEventMaxMember.text = event.maxMember.toString()
                        textViewEventAddress.text = event.address
                        textViewEventPhone.text = event.phone
                        textViewEventDescription.text = event.description
                        val firstPhotoUrl = event.photoUrl
                        if (firstPhotoUrl == DEFAULT_NOT_SET) {
                            Picasso.get().load(R.drawable.default_image_not_set).into(imageViewEvent)
                        } else {
                            Picasso.get().load(firstPhotoUrl).into(imageViewEvent)
                        }
                    }
                })
    }

    private fun fetchOwnerEvent(id: String) {
        mDatabaseReference?.child(CHILD_USERS)?.child(id)?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                textViewEventOwner.text = user?.nama
            }

        })
    }
}
