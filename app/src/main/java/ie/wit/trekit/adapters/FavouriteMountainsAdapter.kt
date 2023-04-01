package ie.wit.trekit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.wit.trekit.databinding.CardMountainBinding
import ie.wit.trekit.models.MountainModel
import timber.log.Timber.i

interface FavMountainListener {


    fun onMountainClick(mountain: MountainModel)


}

class FavouriteMountainsAdapter(private var favouriteMountains: MutableList<MountainModel>, private val listener: FavMountainListener) :
    RecyclerView.Adapter<FavouriteMountainsAdapter.MainHolder>() {
    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val favouriteMountain = favouriteMountains[holder.adapterPosition]
        holder.bind(favouriteMountain, listener)
    }

    override fun getItemCount(): Int = favouriteMountains.size

    class MainHolder(private val binding: CardMountainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //binding data from mountainModel to textviews
        fun bind(favouriteMountain: MountainModel, listener: FavMountainListener) {
            binding.mountainName.text = favouriteMountain.mountainName
            binding.elevation.text = favouriteMountain.elevation.toString()
            binding.root.setOnClickListener{
                listener.onMountainClick(favouriteMountain)
            }
        }
    }
    //deleting item from list
    fun deleteItem(position: Int) {
            val mountain = favouriteMountains[position]
            deleteFavouriteFromFirebase(mountain, false)
            notifyItemRemoved(position)
    }

    //deleting item from firebase
    private fun deleteFavouriteFromFirebase(mountain: MountainModel, isFavourite: Boolean) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userFavouritesRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("user_favourites/$userId")
            mountain.isFavourite = isFavourite
            userFavouritesRef.child(mountain.mountainName).setValue(mountain)
            if (isFavourite) {
                i("Item True")
                userFavouritesRef.child(mountain.mountainName).setValue(true)
            } else {
                i("Item Removed")
                userFavouritesRef.child(mountain.mountainName).removeValue()
            }
        }

    fun updateList(filteredMountains: List<MountainModel>) {
        favouriteMountains = filteredMountains as MutableList<MountainModel>
        notifyDataSetChanged()
    }

//function to delete when user swipes left or right on the item in the recycler view
    fun attachSwipeToDelete(recyclerView: RecyclerView) {
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteItem(position)
                notifyDataSetChanged()
                Toast.makeText(viewHolder.itemView.context, "Deleted", Toast.LENGTH_LONG).show()
            }
        })
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }
}



