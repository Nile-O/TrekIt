package ie.wit.trekit.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MountainFireStore(context: Context) : MountainStore {
    val mountains = ArrayList<MountainModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference


    override suspend fun findAll(): List<MountainModel> {
        return mountains
    }

    override suspend fun findOneMountainByName(mountainName: String): MountainModel? {
        return mountains.find { it.mountainName == mountainName }
    }
    override suspend fun findById(fbId: String): MountainModel? {
        return mountains.find { it.fbId == fbId }
    }
    override suspend fun findByid(id: Long): MountainModel? {
        return mountains.find { it.id == id }
    }
    override suspend fun clear(){
        mountains.clear()
    }

    override suspend fun create(mountain: MountainModel) {
        val key = db.child("users").child(userId).child("mountains").push().key
        key?.let {
            mountain.fbId = key
            mountains.add(mountain)
            db.child("users").child(userId).child("mountains").child(key).setValue(mountain)
        }
    }

    fun fetchMountains(mountainsReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.mapNotNullTo(mountains) {
                    return@mapNotNullTo it.getValue(
                        MountainModel::class.java
                    )
                }
                mountainsReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        st = FirebaseStorage.getInstance().reference
        db = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("mountains")
        db.addListenerForSingleValueEvent(valueEventListener)
    }

    //fun addClimbedMountain(fbId: String, climbedMountain: ClimbedMountain) {
      //  db.child("users").child(userId).child("climbed_mountains").child(fbId).setValue(climbedMountain)
   // }
}