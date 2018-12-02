package id.hobipedia.hobipedia.ui.my_events

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.ui.chat.ChatActivity
import id.hobipedia.hobipedia.ui.event_detail.EventDetailActivity
import id.hobipedia.hobipedia.util.Constant
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_ID_EVENT
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_NAMA_EVENT
import kotlinx.android.synthetic.main.item_event_saya.view.*

class EventSayaAdapter(val listPesanan: ArrayList<Event>, val mContext: Context)
    : RecyclerView.Adapter<EventSayaAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event_saya, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listPesanan.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listPesanan[position]

        val firstPhotoUrl = item.photoUrl
        if (firstPhotoUrl == Constant.DEFAULT.DEFAULT_NOT_SET) {
            Picasso.get().load(R.drawable.default_image_not_set).into(holder.eventImage)
        } else {
            Picasso.get().load(firstPhotoUrl).into(holder.eventImage)
        }

        holder.eventName.text = item.name
        holder.eventDescription.text = item.description

        holder.buttonChat.setOnClickListener {
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra(Constant.KEY.KEY_ID_EVENT, item.eventId)
            intent.putExtra(Constant.KEY.KEY_NAMA_EVENT, item.name)
            intent.putExtra(Constant.KEY.KEY_ID_EVENT, item.eventId)
            intent.putExtra("lat", item.latitude)
            intent.putExtra("lng", item.longitude)
            intent.putExtra("my_event", true)
            mContext.startActivity(intent)
        }

        holder.buttonDetail.setOnClickListener {
            val intent = Intent(mContext, EventDetailActivity::class.java)
            intent.putExtra(KEY_ID_EVENT, item.eventId)
            intent.putExtra(KEY_NAMA_EVENT, item.name)
            intent.putExtra(KEY_ID_EVENT, item.eventId)
            intent.putExtra("lat", item.latitude)
            intent.putExtra("lng", item.longitude)
            intent.putExtra("is_join", true)
            mContext.startActivity(intent)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView = itemView.cardView
        val eventImage = itemView.imageViewThumbnail
        val eventName = itemView.textViewTitle
        val eventDescription = itemView.textViewDescription
        val buttonChat = itemView.buttonChat
        val buttonDetail = itemView.buttonDetail
    }

}