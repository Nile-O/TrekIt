package ie.wit.trekit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.trekit.databinding.CardMountainBinding
import ie.wit.trekit.models.MountainModel

interface FavMountainListener {


    fun onMountainClick(mountain: MountainModel)


}

class FavouriteMountainsAdapter(private var favouriteMountains: List<MountainModel>, private val listener: FavMountainListener) :
    RecyclerView.Adapter<FavouriteMountainsAdapter.MainHolder>() {

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

            fun bind(favouriteMountain: MountainModel, listener: FavMountainListener) {
                binding.mountainName.text = favouriteMountain.mountainName
                binding.elevation.text = favouriteMountain.elevation.toString()
                binding.root.setOnClickListener{
                    listener.onMountainClick(favouriteMountain)
                }
            }
        }

    }



