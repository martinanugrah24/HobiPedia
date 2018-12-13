package id.hobipedia.hobipedia.ui.category

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.Event
import id.hobipedia.hobipedia.util.Constant.DEFAULT.DEFAULT_NOT_SET

class CategoryAdapter(
        val items: ArrayList<Event>, val listener: CategoryListener, val mContext: Context
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.name
        holder.descriptionTextView.text = item.description
        val firstPhotoUrl = item.photoUrl
        if (firstPhotoUrl == DEFAULT_NOT_SET) {
            Picasso.get().load(R.drawable.default_image_not_set).into(holder.thumbnailImageView)
        } else {
            Picasso.get().load(firstPhotoUrl).into(holder.thumbnailImageView)
        }
        holder.joinButton.setOnClickListener {
            listener.onJoinClick(item.eventId, item.name, item.latitude, item.longitude)
        }
        holder.detailButton.setOnClickListener {
            listener.onItemClick(item.eventId, item.name, item.latitude, item.longitude)
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        // MARK: - Public Properties
        val cardView: CardView
        val titleTextView: TextView
        val descriptionTextView: TextView
        val thumbnailImageView: ImageView
        val joinButton: Button
        val detailButton: Button

        // MARK: - Initialization
        init {
            cardView = itemView?.findViewById(R.id.cardView) as CardView
            titleTextView = itemView.findViewById(R.id.textViewTitle) as TextView
            descriptionTextView = itemView.findViewById(R.id.textViewDescription) as TextView
            thumbnailImageView = itemView.findViewById(R.id.imageViewThumbnail) as ImageView
            joinButton = itemView.findViewById(R.id.buttonJoin) as Button
            detailButton = itemView.findViewById(R.id.buttonDetail) as Button
        }
    }
}