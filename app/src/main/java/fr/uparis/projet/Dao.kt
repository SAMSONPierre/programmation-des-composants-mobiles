package fr.uparis.projet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    /** Insertions **/
    // ajouter une langue sans la cle


    // ajouter une langue
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguage(vararg lang: Language): List<Long>

    // ajouter un mot sans la cle


    // ajouter la traduction d'un mot (Word)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWord(vararg word: Word): List<Long>

    // ajouter un dictionnaire sans la cle


    // ajouter un dictionnaire
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertDictionary(vararg dic: Dictionary): List<Long>

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertWordDicAssociation(vararg wordDicAssociation: WordDicAssociation): List<Long>



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

    // TODO : serait-il interessant d'avoir une requete sur WordDicAssociation ?
    @Query("SELECT * FROM WordDicAssociation")
    fun loadAllWordDicAssociation(): LiveData<List<WordDicAssociation>>

    @Query("SELECT idDic FROM Dictionary ORDER BY idDic DESC LIMIT 1")
    fun getLastDic(): LiveData<Long>

    @Query("SELECT idWord FROM Word ORDER BY idWord DESC LIMIT 1")
    fun getLastWord(): LiveData<Long>

}
