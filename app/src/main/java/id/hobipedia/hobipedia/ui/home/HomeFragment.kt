package id.hobipedia.hobipedia.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.extension.toast
import id.hobipedia.hobipedia.model.Category
import id.hobipedia.hobipedia.ui.category.*
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_ID_CATEGORY
import id.hobipedia.hobipedia.util.Constant.KEY.KEY_NAMA_CATEGORY
import kotlinx.android.synthetic.main.fragment_home.*
import id.hobipedia.hobipedia.util.SpacesItemDecoration

class HomeFragment : Fragment(), HomeListener {
    private var mSampleImages = intArrayOf(
            R.drawable.iklan,
            R.drawable.iklan,
            R.drawable.iklan,
            R.drawable.iklan,
            R.drawable.iklan
    )
    private var mCategories = Category.getCategories()
    private var mCarouselView: CarouselView? = null
    private var mImageListener: ImageListener = object : ImageListener {
        override fun setImageForPosition(position: Int, imageView: ImageView?) {
            imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView?.setImageResource(mSampleImages[position])
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup carouselView
        mCarouselView = view.findViewById(R.id.carouselView)
        mCarouselView?.setImageListener(mImageListener)
        mCarouselView?.pageCount = mSampleImages.count()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = HomeAdapter(mCategories, this)
        val spacingInPixels = 2
        recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        recyclerView.isNestedScrollingEnabled = false
    }

    override fun onItemClick(category: Category) {
        navigateToCategoryActivity(categoryId = category.id, categoryName = category.name)
    }

    private fun navigateToCategoryActivity(categoryId: Int, categoryName: String) {
        val intent = Intent(context, CategoryActivity::class.java)
        intent.putExtra(KEY_ID_CATEGORY, categoryId)
        intent.putExtra(KEY_NAMA_CATEGORY, categoryName)
        startActivity(intent)
    }
}
