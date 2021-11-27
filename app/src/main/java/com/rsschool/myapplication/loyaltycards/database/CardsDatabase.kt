package com.rsschool.myapplication.loyaltycards.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard

@Database(entities = [LoyaltyCard::class], version = 1)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun getLoyaltyCardDao() : LoyaltyCardDao

}