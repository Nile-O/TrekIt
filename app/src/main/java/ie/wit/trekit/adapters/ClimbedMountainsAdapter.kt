package ie.wit.trekit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.wit.trekit.R
import ie.wit.trekit.activities.AddClimbedDetailsActivity
import ie.wit.trekit.databinding.CardClimbedMountainBinding
import ie.wit.trekit.models.ClimbedMountain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber.i

interface ClimbedMountainListener {
    suspend fun onClimbedMountainClick(mountain: ClimbedMountain)
}

class ClimbedMountainsAdapter(private var climbedMountains: MutableList<ClimbedMountain>, private val listener: ClimbedMountainListener) :
    RecyclerView.Adapter<ClimbedMountainsAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardClimbedMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val climbedMountain = climbedMountains[holder.adapterPosition]
        holder.bind(climbedMountain, listener)
    }

    override fun getItemCount(): Int = climbedMountains.size

    class MainHolder(private val binding: CardClimbedMountainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(climbedMountain: ClimbedMountain, listener: ClimbedMountainListener) {
            binding.mountainName.text = climbedMountain.mountainName
            binding.dateOfClimbInput.text = climbedMountain.dateClimbed
            binding.duration.text = climbedMountain.duration.toString()
            binding.root.setOnClickListener{
                GlobalScope.launch(Dispatchers.Main) { listener.onClimbedMountainClick(climbedMountain)}
            }
        }
    }

    fun deleteItem(position: Int) {
        val climbedMountain = climbedMountains[position]
        climbedMountain.key?.let { deleteClimbedFromFirebase(it) }
        climbedMountains.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, climbedMountains.size - position)
    }

    private fun deleteClimbedFromFirebase(key: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userClimbedRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("user_climbed_mountains/$userId")
        userClimbedRef.child(key).removeValue()
        i("item deleted $key")
    }

    fun updateList(newList: List<ClimbedMountain>) {
        climbedMountains = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun attachSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
