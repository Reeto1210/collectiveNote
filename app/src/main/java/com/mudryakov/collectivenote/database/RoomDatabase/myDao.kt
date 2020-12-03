package com.mudryakov.collectivenote.database.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mudryakov.collectivenote.models.PaymentModel

@Dao
interface myDao {
    @Query("SELECT *FROM allPayments ORDER BY time DESC")
    fun getAllPayments():LiveData<List<PaymentModel>>

    @Query("DELETE FROM allPayments")
   suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insertAll(list:List<PaymentModel>)
}