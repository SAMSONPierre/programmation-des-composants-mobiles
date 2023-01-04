package fr.uparis.projet

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import fr.uparis.projet.databinding.FragmentListWordBinding
import fr.uparis.projet.databinding.FragmentListWordListBinding
import java.nio.file.Files.size

class MyWordRecyclerViewAdapter(val wordFragmentBinding: FragmentListWordListBinding, var words: List<Word>?, private val model: MainViewModel) : RecyclerView.Adapter<MyWordRecyclerViewAdapter.VH>() {
    class VH(val binding: FragmentListWordBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var word: Word
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding=FragmentListWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if(words!=null){
            /* on remplit nos mots */
            holder.word=words!![position]
            with(holder.binding){
                name.text=holder.word.word
                url.text=holder.word.urlToTranslation
            }

            /* on ouvre la page de traduction du mot */
            holder.itemView.setOnClickListener{
                if(model.selectedWords.contains(holder.word))
                    model.selectedWords.remove(holder.word)
                else
                    model.selectedWords.add(holder.word)
                wordFragmentBinding.reviewButton.isEnabled = (model.selectedWords.size==1)
                notifyDataSetChanged()
            }

            if(model.selectedWords.contains(holder.word)){
                (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmonDarkest))
            }
            else{
                when (position % 2) {
                    0 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmon))
                    1 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmonDarker))
                }
            }
        }
    }

    override fun getItemCount(): Int = words?.size?:0
}