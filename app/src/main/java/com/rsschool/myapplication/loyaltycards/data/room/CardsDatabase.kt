package com.rsschool.myapplication.loyaltycards.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard

@Database(entities = [LoyaltyCard::class], version = 2)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun getLoyaltyCardDao() : LoyaltyCardDao
}