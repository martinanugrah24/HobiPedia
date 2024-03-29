package id.hobipedia.hobipedia.ui.category

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.Category
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.ui.add_event.AddEventActivity
import id.hobipedia.hobipedia.ui.event_detail.EventDetailActivity
import id.hobipedia.hobipedia.util.Constant.CHILD.*
import id.hobipedia.hobipedia.util.Constant.KEY.*
import kotlinx.android.synthetic.main.activity_category.*
import id.hobipedia.hobipedia.ui.chat.ChatActivity
import id.hobipedia.hobipedia.util.NetworkAvailable
import java.util.HashMap

class CategoryActivity : AppCompatActivity(), CategoryListener {

    private var mActionBar: ActionBar? = null
    private var mExtras: Bundle? = null
    private var mCategoryId: Int? = null

    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        setupFirebase()
        mActionBar = supportActionBar
        mActionBar?.setDisplayHomeAsUpEnabled(true)

        statusTextView.visibility = View.GONE

        mExtras = intent.extras
        mCategoryId = mExtras?.getInt(KEY_ID_CATEGORY)
        mCategory = getCurrentCategory()
        mActionBar?.title = mCategory?.name

        setupRecyclerView()

        if (NetworkAvailable.isNetworkAvailable(this)) {
            fetchEvents(mCategory?.name!!)
        } else {
            toast("Koneksi internet tidak tersedia")
            progressBar.let {
                it.visibility = View.GONE
            }
        }

        floatingActionButton.setOnClickListener {
            navigateToAddEventActivity(mCategory?.name!!)
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

    private fun getCurrentCategory(): Category? {
        Category.getCategories().forEach {
            if (it.id == mCategoryId) {
                return it
            }
        }
        return null
    }

    private var mAdapter: CategoryAdapter? = null

    private fun getDefaultLinearLayoutManager(): LinearLayoutManager {
        val reverseLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reverseLinearLayoutManager.stackFromEnd = false
        return reverseLinearLayoutManager
    }

    private fun getReverseLinearLayoutManager(): LinearLayoutManager {
        val reverseLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        reverseLinearLayoutManager.stackFromEnd = true
        return reverseLinearLayoutManager
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = getReverseLinearLayoutManager()
        mAdapter = CategoryAdapter(mEvents, this, this)
        recyclerView.adapter = mAdapter
    }

    private fun updateUI() {
        progressBar.visibility = View.GONE
        if (mEvents.count() == 0) {
            statusTextView.visibility = View.VISIBLE
        } else {
            statusTextView.visibility = View.GONE
        }
    }

    override fun onItemClick(id: String, name: String, lat: Double, lng: Double) {
        navigateToEventDetailActivity(id, name, lat, lng)
    }

    override fun onJoinClick(id: String, name: String, lat: Double, lng: Double) {
        var count: Int
        val map = HashMap<String, Any?>()
        mDatabaseReference?.child(CHILD_EVENTS)?.child(id)?.child("members")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                count = (p0.childrenCount - 1).toInt().inc()
                map.put(count.toString(), mFirebaseAuth?.currentUser?.uid)
                mDatabaseReference?.child(CHILD_EVENTS)?.child(id)?.child("members")?.updateChildren(map)
            }

        })
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(KEY_ID_EVENT, id)
        intent.putExtra(KEY_NAMA_EVENT, name)
        intent.putExtra("lat", lat)
        intent.putExtra("lng", lng)
        startActivity(intent)
        toast("Berhasil join")
        finish()
    }

    private fun navigateToAddEventActivity(categoryName: String) {
        val intent = Intent(this, AddEventActivity::class.java)
        intent.putExtra(KEY_NAMA_CATEGORY, categoryName)
        startActivity(intent)
    }

    private fun navigateToEventDetailActivity(eventId: String, eventName: String, lat: Double, lng: Double) {
        val intent = Intent(this, EventDetailActivity::class.java)
        intent.putExtra(KEY_ID_EVENT, eventId)
        intent.putExtra(KEY_NAMA_EVENT, eventName)
        intent.putExtra("lat", lat)
        intent.putExtra("lng", lng)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            finish()
        }
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

    private var mEvents = arrayListOf<Event>()

    private fun fetchEvents(categoryName: String) {
        mEvents.clear()
        mDatabaseReference?.child(CHILD_EVENTS)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressBar.visibility = View.GONE
                toast(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                progressBar.visibility = View.GONE
                if (!p0.hasChildren()) {
                    statusTextView.visibility = View.VISIBLE
                    return
                }
                for (childDataSnapshot in p0.children) {
                    val item = childDataSnapshot.getValue(Event::class.java)
                    if (item!!.category == categoryName) {
                        if (item.ownerId != mFirebaseUser?.uid && !item.members.contains(mFirebaseAuth?.currentUser?.uid)) {
                            mEvents.add(item)
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                    updateUI()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        recreate()
    }
}
