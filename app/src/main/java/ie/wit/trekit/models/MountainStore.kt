package ie.wit.trekit.models

interface MountainStore {

        suspend fun findAll(): List<MountainModel>
        suspend fun create(mountain: MountainModel)
        suspend fun findById(fbId:String) : MountainModel?
        suspend fun findByid(id: Long) : MountainModel?
        suspend fun clear()
    abstract suspend fun findOneMountainByName(mountainName: String): MountainModel?


}