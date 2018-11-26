package id.hobipedia.hobipedia.model

import id.hobipedia.hobipedia.R

class Category(
        val id: Int,
        val name: String,
        val imageResourceId: Int
) {

    companion object {
        fun getCategories(): ArrayList<Category> {
            return arrayListOf(
                    Category(0, "Futsal", R.drawable.hobipedia_footbal_icon),
                    Category(1, "Basket", R.drawable.hobipedia_basketball_icon),
                    Category(2, "Memancing", R.drawable.hobipedia_fishing_icon),
                    Category(3, "Fotografi", R.drawable.hobipedia_photography_icon),
                    Category(4, "Seni", R.drawable.hobipedia_art_icon),
                    Category(5, "Bowling", R.drawable.hobipedia_bowling_icon)
            )
        }
    }

}