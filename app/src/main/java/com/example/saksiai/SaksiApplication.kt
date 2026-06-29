package com.example.saksiai

import android.app.Application
import androidx.room.Room
import com.example.saksiai.data.local.SaksiDatabase

class SaksiApplication : Application() {
    
    val database: SaksiDatabase by lazy {
        Room.databaseBuilder(
            this,
            SaksiDatabase::class.java,
            "saksi_database"
        ).build()
    }
}