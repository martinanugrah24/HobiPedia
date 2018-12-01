package id.hobipedia.hobipedia.ui.chat

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.model.GroupChat
import id.hobipedia.hobipedia.model.Message
import id.hobipedia.hobipedia.model.User
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_EVENTS
import id.hobipedia.hobipedia.util.Constant.CHILD.CHILD_USERS
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_ID_EVENT
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_NAMA_EVENT
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.message_area.*


@Suppress("NAME_SHADOWING")
class ChatActivity : AppCompatActivity() {

    private var databaseRef: DatabaseReference? = null

    private var mExtras: Bundle? = null
    private var mEventId: String? = null
    private var mIsMyEvent: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mExtras = intent.extras
        mEventId = mExtras?.getString(KEY_ID_EVENT)
        title = mExtras?.getString(KEY_NAMA_EVENT)
        mIsMyEvent = mExtras?.getBoolean("my_event")

        invalidateOptionsMenu()

        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hobipedia-b161b.firebaseio.com/groupChat/")
        databaseRef?.keepSynced(true)

        fetchMessage(mEventId!!)

        sendButton.setOnClickListener {
            sendMessage()
        }

    }


    private fun sendMessage() {
        val messageText = messageArea.text.toString()
        val messageId = databaseRef?.push()?.key
        val message = Message(messageId, FirebaseAuth.getInstance().currentUser?.uid!!, messageText)
        if (messageText.trim { it <= ' ' }.isNotEmpty()) {
            databaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var isChecked = false
                    if (!p0.hasChildren()) {
                        val groupId = databaseRef?.push()?.key
                        val groupChat = GroupChat(groupId, mEventId, null)
                        databaseRef?.child(groupId!!)?.setValue(groupChat)
                        databaseRef?.child(groupId!!)?.child("messages")?.child(messageId!!)?.setValue(message)
                    }
                    for (childDataSnapshot in p0.children) {
                        val groupChat = childDataSnapshot.getValue(GroupChat::class.java)
                        if (groupChat!!.eventId == mEventId) {
                            databaseRef?.child(groupChat.groupChatId!!)?.child("messages")?.child(messageId!!)?.setValue(message)
                            isChecked = true
                            break
                        }
                    }
                    if (!isChecked) {
                        val groupId = databaseRef?.push()?.key
                        val groupChat = GroupChat(groupId, mEventId, null)
                        databaseRef?.child(groupId!!)?.setValue(groupChat)
                        databaseRef?.child(groupId!!)?.child("messages")?.child(messageId!!)?.setValue(message)
                    }
                }

            })
            messageArea.setText("")
        }
    }


    private fun fetchMessage(eventId: String) {
        var groupChatId = ""
        databaseRef?.orderByChild("eventId")?.equalTo(eventId)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (childDataSnapshot in p0.children) {
                    val groupChat = childDataSnapshot.getValue(GroupChat::class.java)
                    if (groupChat!!.eventId == eventId) {
                        groupChatId = groupChat.groupChatId
                        Log.d("DEBUG_1", groupChatId)
                    }
                }
                databaseRef?.child(groupChatId)?.child("messages")?.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val message = p0.getValue(Message::class.java)
                        Log.d("DEBUG_2", message.toString())
                        if (message?.ownerId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                            addMessageBox("You:-\n${message?.message}", 1)
                            Log.d("DEBUG_3", message?.message.toString())
                        } else {
                            FirebaseDatabase.getInstance().reference.child(CHILD_USERS).child(message?.ownerId!!).addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    FirebaseDatabase.getInstance().reference.child(CHILD_EVENTS).child(eventId).addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            val event = p0.getValue(Event::class.java)
                                            if (event?.ownerId == user?.userId) {
                                                addMessageBox("${user?.nama} (Admin):-\n${message.message}", 2)
                                                Log.d("DEBUG_4", message.message.toString())
                                            } else {
                                                addMessageBox("${user?.nama}:-\n${message.message}", 2)
                                                Log.d("DEBUG_5", message.message.toString())
                                            }
                                        }

                                    })
                                }

                            })
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }

                })
            }

        })

    }


    private fun updateUI() {

    }


    @SuppressLint("RtlHardcoded")
    fun addMessageBox(message: String, type: Int) {
        val textView = TextView(this@ChatActivity)
        textView.text = message
        val lp2 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp2.weight = 1.0f
        if (type == 1) {
            lp2.gravity = Gravity.RIGHT
            textView.setBackgroundResource(R.drawable.bubble_in)
        } else {
            lp2.gravity = Gravity.LEFT
            textView.setBackgroundResource(R.drawable.bubble_out)
        }
        textView.layoutParams = lp2
        layout1.addView(textView)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_keluar -> {
                exitGroup()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun exitGroup() {
        FirebaseDatabase.getInstance().reference.child(CHILD_EVENTS).child(mEventId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val event = p0.getValue(Event::class.java)
                val members = event?.members
                if (members?.contains(FirebaseAuth.getInstance().currentUser?.uid)!!) {
                    members.remove(FirebaseAuth.getInstance().currentUser?.uid)
                    FirebaseDatabase.getInstance().reference.child(CHILD_EVENTS).child(mEventId!!).child("members").setValue(members)
                }
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_chat, menu)
        val item = menu?.findItem(R.id.menu_keluar)
        item?.isVisible = !mIsMyEvent!!
        return true
    }
}
