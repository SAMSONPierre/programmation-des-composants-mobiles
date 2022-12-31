package fr.uparis.projet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread

class MainViewModel(app: Application): AndroidViewModel(app) {
    val dao = (app as DictionaryApplication).database.dao()

    val listDictionaries=dao.loadAllDictionaries()
    var listDictionariesSpinner=dao.loadDictionariesFromPrefix("")

    /** used for listing words of selected dic **/
    var selectedDic: Dictionary? = null
    var selectedDicWords: LiveData<List<DictionaryWithWords>>? = null
    var listWords: List<Word>? = null

    /** used for daily learning **/
    var wordsOfTheDay = mutableListOf<Word>()

    /** for deleting on swipe **/
    var swipedDictionary: Dictionary? = null
    var swipedWord: Word? = null

    /** for special learning sessions **/
    var listPairLanguages=dao.loadLanguagesPairs()

    fun loadWordsOfSelectedDic(idDic: Long){
        selectedDicWords=dao.loadWordsFromDic(idDic)
        listWords=selectedDicWords?.value?.get(0)?.words
    }

    fun loadPartialName(prefix: String): LiveData<List<String>>{
        listDictionariesSpinner=dao.loadDictionariesFromPrefix(prefix)
        Log.d("spinner 0", listDictionariesSpinner.value.toString())
        return listDictionariesSpinner
    }


    /** select 10 random words from all available words **/
    fun getDailyWords(): ArrayList<String> {
        wordsOfTheDay = dao.loadAllWordsTranslations()
            .asSequence().shuffled().take(10).toMutableList()
        val wordsName = mutableListOf<String>()
        if(wordsOfTheDay.count() >=10) for(i in 0 until 10) wordsName.add(wordsOfTheDay[i].word)
        return ArrayList(wordsName)
    }

    /** get daily words' translations, always used after getDailyWords() **/
    fun getDailyWordsTranslation(): ArrayList<String> {
        val wordsUrl = mutableListOf<String>()
        for(i in 0 until 10) wordsUrl.add(wordsOfTheDay[i].urlToTranslation)
        return ArrayList(wordsUrl)
    }

    fun getDailyWordsLangSrc(): ArrayList<String> {
        val wordsLangSrc = mutableListOf<String>()
        for(i in 0 until 10) wordsLangSrc.add(wordsOfTheDay[i].lang_src)
        return ArrayList(wordsLangSrc)
    }

    fun getDailyWordsLangDst(): ArrayList<String> {
        val wordsLangDst = mutableListOf<String>()
        for(i in 0 until 10) wordsLangDst.add(wordsOfTheDay[i].lang_dst)
        return ArrayList(wordsLangDst)
    }
    /** end of daily words' info **/

    /** delete word **/
    fun deleteWord() {
        thread {
            dao.deleteWord(swipedWord!!) // ne supprime pas le dictionnaire s'il est vide
            swipedWord = null
        }
    }

    /** delete dictionary **/
    fun deleteDictionary(){
        thread{
            Log.d("words", "id = " + swipedDictionary!!.idDic)
            val toDelete = dao.loadWordsToDelete(swipedDictionary!!.idDic)
            for(word: Word in toDelete) dao.deleteWord(word)
            dao.deleteDictionaryId(swipedDictionary!!.idDic)
            dao.deleteDictionary(swipedDictionary!!)
            swipedDictionary=null
        }
    }
}