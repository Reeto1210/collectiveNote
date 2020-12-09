package com.mudryakov.collectivenote.database.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel

@Dao
interface myDao {

    @Transaction
    suspend fun updateAllPayments(listPayments: List<PaymentModel>){
        deleteAllPayments()
        insertAllPayments(listPayments)

    }
    @Transaction
    suspend fun updateAllUsers(listOfUsers: List<UserModel>){
        deleteAllUsers()
        insertAllUsers(listOfUsers)

    }

    @Query("SELECT *FROM AllPayments")
       fun getAllPayments():LiveData<List<PaymentModel>>

    @Query("DELETE FROM AllPayments")
   suspend fun deleteAllPayments()

    @Delete
    suspend fun deleteOnePayment(paymentModel: PaymentModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insertAllPayments(list:List<PaymentModel>)

    @Query("SELECT *FROM AllUsers")
    fun getAllUsers():LiveData<List<UserModel>>

    @Query("DELETE FROM AllUsers")
    suspend fun deleteAllUsers()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insertAllUsers(list:List<UserModel>)
}