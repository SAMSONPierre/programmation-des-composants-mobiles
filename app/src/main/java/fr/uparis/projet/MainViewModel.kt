package fr.uparis.projet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread

class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()
    val listDictionaries=dao.loadAllDictionaries()
    var selectedDic: Dictionary? = null
    var selectedDicWords: LiveData<List<DictionaryWithWords>>? = null
    var listWords: List<Word>? = null

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
        listWords=selectedDicWords?.value?.get(0)?.words
    }
}