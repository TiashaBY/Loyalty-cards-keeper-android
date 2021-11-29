package com.rsschool.myapplication.loyaltycards.di

import android.app.Application
import androidx.room.Room
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.rsschool.myapplication.loyaltycards.datasource.repository.CardsRepository
import com.rsschool.myapplication.loyaltycards.datasource.repository.RoomCardsRepository
import com.rsschool.myapplication.loyaltycards.datasource.room.CardsDatabase
import com.rsschool.myapplication.loyaltycards.datasource.room.LoyaltyCardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun getBarCodeScannerOptions() = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, CardsDatabase::class.java, "cards_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLoyaltyCardsDao(db : CardsDatabase) = db.getLoyaltyCardDao()

    @Provides
    @Singleton
    fun provideCardsRepo(dao: LoyaltyCardDao) : CardsRepository = RoomCardsRepository(dao)

/*    @Provides
    @Singleton
    fun provideRepo(context: Application) = UserRepository()*/
}