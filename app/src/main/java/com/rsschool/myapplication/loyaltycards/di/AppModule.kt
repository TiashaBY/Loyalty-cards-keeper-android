package com.rsschool.myapplication.loyaltycards.di

import android.content.Context
import androidx.room.Room
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.rsschool.myapplication.loyaltycards.database.CardsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CardsDatabase::class.java, "cards_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLoyaltyCardsDao(db : CardsDatabase) = db.getLoyaltyCardDao()



/*    @Provides
    @Singleton
    fun provideRepo(context: Application) = UserRepository()*/
}