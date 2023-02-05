package ie.wit.trekit.models

interface MountainStore {

        fun findAll(): List<MountainModel>
        fun create(mountain: MountainModel)

}