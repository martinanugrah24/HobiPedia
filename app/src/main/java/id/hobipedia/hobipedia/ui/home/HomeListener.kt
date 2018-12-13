package id.hobipedia.hobipedia.ui.home

import id.hobipedia.hobipedia.model.Category
interface HomeListener {
    fun onItemClick(category: Category)
}