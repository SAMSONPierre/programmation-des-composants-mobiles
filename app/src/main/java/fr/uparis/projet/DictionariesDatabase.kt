package fr.uparis.projet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[Language::class, Word::class, Dictionary::class, WordDicAssociation::class], version=1)
abstract class DictionariesDatabase: RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        @Volatile
        private var instance: DictionariesDatabase?=null

        fun getDB(context: Context): DictionariesDatabase{
            if(instance==null){
                val db= Room.databaseBuilder(context.applicationContext, DictionariesDatabase::class.java, "dictionaries")
                    .fallbackToDestructiveMigration()
                    .build()
                instance=db
            }
            return instance!!
        }
    }

}