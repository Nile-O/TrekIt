package ie.wit.trekit.models

import timber.log.Timber.i

class MountainMemStore : MountainStore {

    val mountains = ArrayList<MountainModel>()

    override fun findAll(): List<MountainModel> {
        return mountains
    }

    override fun create(mountain: MountainModel) {
        mountains.add(mountain)
        logAll()
    }
    private fun logAll() {
        mountains.forEach{ i("${it}") }
    }
}