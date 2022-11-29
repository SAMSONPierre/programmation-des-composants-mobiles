package fr.uparis.projet

import android.app.Application
import android.provider.UserDictionary
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()
    val listDictionaries=dao.loadAllDictionaries()
    var selectedDicWords: LiveData<List<Word>>? = null

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
    }
}