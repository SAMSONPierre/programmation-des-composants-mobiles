package fr.uparis.projet

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import fr.uparis.projet.databinding.FragmentListDictionaryBinding

class MyDictionaryRecyclerViewAdapter(var dictionaries: List<Dictionary>, private val model: MainViewModel):
    RecyclerView.Adapter<MyDictionaryRecyclerViewAdapter.VH>() {
        class VH(val binding: FragmentListDictionaryBinding): RecyclerView.ViewHolder(binding.root){
            lateinit var dictionary: Dictionary
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val binding=FragmentListDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VH(binding)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            /* on remplit nos dicos */
            holder.dictionary=dictionaries[position]
            with(holder.binding){
                langSrc.text=holder.dictionary.lang_src
                langDst.text=holder.dictionary.lang_dst
                url.text=holder.dictionary.urlPrefix
            }

            if(holder.dictionary==model.selectedDic){
                (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmonDarkest))
            }
            else {
                when (position % 2) {
                    0 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmon))
                    1 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmonDarker))
                }
            }

            holder.itemView.setOnClickListener{
                if(model.selectedDic!=holder.dictionary) model.selectedDic=holder.dictionary
                else model.selectedDic=null
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = dictionaries.size
}