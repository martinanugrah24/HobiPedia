package id.hobipedia.hobipedia.ui.home

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.model.Category
import java.util.ArrayList

class HomeAdapter(val items: ArrayList<Category>, val listener: HomeListener) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Picasso.get().load(item.imageResourceId).into(holder.imageView)
        holder.categoryTextView.text = item.name
        holder.cardView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        // MARK: - Public Properties
        val cardView: CardView
        val imageView: ImageView
        val categoryTextView: TextView

        // MARK: - Initialization
        init {
            cardView = itemView?.findViewById(R.id.cardView) as CardView
            imageView = itemView.findViewById(R.id.categoryImageView)
            categoryTextView = itemView.findViewById(R.id.categoryTextView)
        }
    }
}