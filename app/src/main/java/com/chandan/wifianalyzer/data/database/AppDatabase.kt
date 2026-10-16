package com.chandan.wifianalyzer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chandan.wifianalyzer.data.model.SignalMeasurement

@Database(
    entities = [SignalMeasurement::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun signalMeasurementDao(): SignalMeasurementDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wifi_analyzer_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}