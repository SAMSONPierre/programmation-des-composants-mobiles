package fr.uparis.projet

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class SauvegardeViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as DictionaryApplication).database.dao()

    fun insertLanguage(l_name : String) {
        dao.insertLanguage(Lang(name = l_name.trim()))
    }

    fun insertWord(w_name : String,src_name : String,dst_name : String,url : String): Long {
        val l = dao.insertWord(WordInfo2(word = w_name.trim(), lang_src = src_name.trim(), lang_dst = dst_name.trim(), urlToTranslation = url.trim(), 0))
        return l[0]
    }

    fun insertDictionnary(src_name : String,dst_name : String,url : String): Long {
        val l = dao.insertDictionary(DictionaryLang(lang_src = src_name.trim(), lang_dst = dst_name.trim(), urlPrefix = url.trim()))
        return l[0]
    }

    fun insertWordDicAssociation(id_word: Long, id_dic: Long): List<Long>{
        val l = dao.insertWordDictionary(WordDicAssociation(id_word,id_dic))
        return l
    }

    fun getDicID(url: String): Long{
        return dao.getDicID(url)
    }
}