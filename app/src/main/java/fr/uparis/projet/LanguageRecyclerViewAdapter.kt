package fr.uparis.projet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.projet.databinding.ConfigSpecialSessionBinding
import fr.uparis.projet.databinding.LanguageRecyclerItemBinding

class LanguageRecyclerViewAdapter(val fragmentManager: FragmentManager, var pairs: List<LanguagePair>?, private val model: MainViewModel) : RecyclerView.Adapter<LanguageRecyclerViewAdapter.VH>() {

    class VH(val binding: LanguageRecyclerItemBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var pair: LanguagePair
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding=LanguageRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if(pairs!=null){
            /* on remplit nos paires de langues */
            holder.pair=pairs!![position]
            with(holder.binding){
                langSrc.text=holder.pair.lang_src
                langDst.text=holder.pair.lang_dst
            }

            /* TODO on ouvre un popup de configuration */
            holder.itemView.setOnClickListener{
                val configDialog = ConfigFragment()
                configDialog.show(fragmentManager, "CONFIG")
            }

            when (position % 2) {
                0 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmon))
                1 -> (holder.itemView as CardView).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.salmonDarker))
            }
        }
    }

    override fun getItemCount(): Int = pairs?.size?:0
}