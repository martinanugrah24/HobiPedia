package id.hobipedia.hobipedia.ui.my_events

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_EVENTS
import kotlinx.android.synthetic.main.fragment_event_lain.*

class EventLainFragment : android.support.v4.app.Fragment() {

    lateinit var mAdapter: EventLainAdapter
    lateinit var mEvents: ArrayList<Event>

    lateinit var mStatusTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_saya, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchEvent()

    }


    private fun fetchEvent() {
        mEvents = arrayListOf()

        setupRecyclerView()
        progressBar?.let { it.visibility = View.VISIBLE }
        statusTextView?.let { it.visibility = View.VISIBLE }
        mStatusTextView = activity!!.findViewById(R.id.statusTextView)

        val databaseRef = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        databaseRef.child(CHILD_EVENTS).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressBar?.let { it.visibility = View.INVISIBLE }
            }

            override fun onDataChange(p0: DataSnapshot) {
                progressBar?.let { it.visibility = View.INVISIBLE }
                mEvents.clear()
                for (data in p0.children) {
                    val event = data.getValue(Event::class.java)
                    if (event?.members?.contains(userId)!!)
                        mEvents.add(event)
                }

                if (mEvents.isNotEmpty() || !mEvents.isNullOrEmpty()) {
                    statusTextView?.let { it.visibility = View.GONE }
                } else {
                    statusTextView?.let { it.visibility = View.VISIBLE }
                }

                mAdapter.notifyDataSetChanged()
            }
        })
    }


    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        mAdapter = EventLainAdapter(mEvents, context!!)
        recyclerView.adapter = mAdapter
    }


    override fun onResume() {
        super.onResume()
        mEvents.clear()
        fetchEvent()
    }

}
