package fr.uparis.projet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    /** Insertions **/
    // ajouter une langue sans la cle
    @Insert(entity=Language::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertLanguage(vararg lang: Lang): List<Long>

    // ajouter une langue
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertLanguage(vararg lang: Language): List<Long>

    // ajouter la traduction d'un mot (Word)
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertWord(vararg word: Word): List<Long>

    // ajouter la traduction d'un mot sans cle prmimaire
    @Insert(entity=Word::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertWord(vararg word: WordInfo2): List<Long>

    // ajouter un dictionnaire
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertDictionary(vararg dic: Dictionary): List<Long>

    // ajouter un dictionnaire sans la cle
    @Insert(entity=Dictionary::class, onConflict=OnConflictStrategy.IGNORE)
    fun insertDictionary(vararg dic: DictionaryLang): List<Long>

    // ajouter l'association mot-dico (WordDic)
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertWordDictionary(vararg wd: WordDicAssociation): List<Long>

    // TODO : quelles tables sont susceptibles d'etre mises a jour ?

    /** Suppressions **/
    // supprimer une traduction
    @Delete(entity=Word::class)
    fun deleteWordsTranslations(ids: List<WordInfo>): Int

    // supprimer un dico
    @Delete(entity=Dictionary::class)
    fun deleteDictionaries(ids: List<DictionaryInfo>): Int

    /** Requetes **/
    // generer toutes les traductions
    @Query("SELECT * FROM Word")
    fun loadAllWordsTranslations(): LiveData<List<Word>>

    // generer tous les dictionnaires
    @Query("SELECT * FROM Dictionary")
    fun loadAllDictionaries(): LiveData<List<Dictionary>>

    // selectionner les mots d'un dictionnaire
    @Query("SELECT Word.idWord, Word.word, Word.lang_src, Word.lang_dst, Word.urlToTranslation " +
            "FROM Word " +
            "NATURAL JOIN WordDicAssociation " +
            "WHERE WordDicAssociation.idDic = :idDictionary")
    fun loadWordsFromDic(idDictionary: Long): LiveData<List<Word>>

    @Transaction
    @Query("SELECT * FROM Dictionary")
    fun loadWordsFromDic(): LiveData<List<WordDictionaryPair>>

    @Query("SELECT idDic FROM Dictionary WHERE Dictionary.urlPrefix = :url")
    fun getLastDic(url : String): Long

    @Query("SELECT idWord FROM Word WHERE word = :nom AND lang_src = :langSRC AND lang_dst = :langDST")
    fun getLastWord(nom : String,langSRC : String,langDST : String ): Long
}
