package ie.wit.trekit.models

import timber.log.Timber.i

class MountainMemStore : MountainStore {

    val mountains = ArrayList<MountainModel>()

    override suspend fun findAll(): List<MountainModel> {
        return mountains
    }

    override suspend fun create(mountain: MountainModel) {
        mountains.add(mountain)
        logAll()
    }
    override suspend fun findById(fbId: String): MountainModel? {
        return mountains.find { it.fbId == fbId }
    }
    override suspend fun findByid(id: Long): MountainModel? {
        val foundMountain: MountainModel? = mountains.find { it.id == id }
        return foundMountain
    }

    override suspend fun clear(){
        mountains.clear()
    }

    override suspend fun findOneMountainByName(mountainName: String): MountainModel? {
        TODO("Not yet implemented")
    }

    private fun logAll() {
        mountains.forEach{ i("${it}") }
    }
}