package com.tutorial.kneecast.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tutorial.kneecast.data.local.dao.SavedAddressDao
import com.tutorial.kneecast.data.local.entity.SavedAddress

@Database(
    entities = [SavedAddress::class],
    version = 1,
    exportSchema = true
)
abstract class SavedAddressDatabase : RoomDatabase() {
    abstract fun savedAddressDao(): SavedAddressDao

    companion object {
        private const val DATABASE_NAME = "saved_address_db"
        
        @Volatile
        private var INSTANCE: SavedAddressDatabase? = null

        fun getInstance(context: Context): SavedAddressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedAddressDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}