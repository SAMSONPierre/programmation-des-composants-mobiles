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
        Thread {
            val l = dao.insertLanguage(Lang(name = l_name.trim()))
            insertInfo.postValue(if (l[0] == -1L) 0 else 1)
            Log.d(ContentValues.TAG, "insert ${insertInfo.value} elements")
        }.start()
    }

    fun insertWord(w_name : String,src_name : String,dst_name : String,url : String) {
        Thread {
            val l = dao.insertWord(WordInfo2(word = w_name.trim(), lang_src = src_name.trim(), lang_dst = dst_name.trim(), urlToTranslation = url.trim() ))
            insertInfo.postValue(if (l[0] == -1L) 0 else 1)
            Log.d(ContentValues.TAG, "insert ${insertInfo.value} elements")

        }.start()
    }
    fun insertDictionnary(src_name : String,dst_name : String,url : String)  {
        Thread {
            val l = dao.insertDictionary(DictionaryLang(lang_src = src_name.trim(), lang_dst = dst_name.trim(), urlPrefix = url.trim()))
            insertInfo.postValue(if (l[0] == -1L) 0 else 1)
            Log.d(ContentValues.TAG, "insert ${insertInfo.value} elements")
        }.start()
    }

    fun insertWordDicAssociation(id_word: Long, id_dic: Long){
        Thread {
            val l = dao.insertWordDictionary(WordDicAssociation(id_word,id_dic))
            insertInfo.postValue(if (l[0] == -1L) 0 else 1)
            Log.d(ContentValues.TAG, "insert ${insertInfo.value} elements")
        }.start()
    }

    fun getLastWord() : LiveData<Long>{
        return dao.getLastWord()
    }

    fun getLastDic() : LiveData<Long>{
        return dao.getLastDic()
    }

    val insertInfo = MutableLiveData<Int>(0)
}