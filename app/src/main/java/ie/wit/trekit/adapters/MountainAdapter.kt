package ie.wit.trekit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.trekit.databinding.CardMountainBinding
import ie.wit.trekit.models.MountainModel

interface MountainListener {


    fun onMountainClick(mountain: MountainModel)

}

class MountainAdapter(private var mountains: List<MountainModel>, private val listener: MountainListener) :
    RecyclerView.Adapter<MountainAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val mountain = mountains[holder.adapterPosition]
        holder.bind(mountain, listener)
    }

    override fun getItemCount(): Int = mountains.size

    class MainHolder(private val binding: CardMountainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mountain: MountainModel, listener: MountainListener) {
            binding.mountainName.text = mountain.mountainName
            binding.elevation.text = mountain.elevation.toString()
            binding.root.setOnClickListener{
                listener.onMountainClick(mountain)
            }
        }
    }

}