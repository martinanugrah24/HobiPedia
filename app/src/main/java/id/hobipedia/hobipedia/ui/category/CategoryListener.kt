package id.hobipedia.hobipedia.ui.category

interface CategoryListener {

    fun onItemClick(id: String, name: String, lat: Double, lng: Double)

    fun onJoinClick(id: String, name: String, lat: Double, lng: Double)

}