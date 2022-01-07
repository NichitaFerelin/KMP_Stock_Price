package com.ferelin.core.data.entity.favouriteCompany

import com.ferelin.core.checkBackgroundThread
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

internal class FavouriteCompanyApi @Inject constructor(
  private val firebaseReference: DatabaseReference,
) {
  fun load(userToken: String): Flow<FavouriteCompanyResponse> = callbackFlow {
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(resultSnapshot: DataSnapshot) {
          val response = FavouriteCompanyResponse(
            data = resultSnapshot.children.map { idSnapshot ->
              idSnapshot.key!!.toInt()
            }
          )
          trySend(response)
        }

        override fun onCancelled(error: DatabaseError) {
          // Do nothing
        }
      })
    awaitClose()
  }

  fun putBy(userToken: String, companyId: Int) {
    checkBackgroundThread()
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .child(companyId.toString())
      .setValue(companyId)
  }

  fun eraseAll(userToken: String) {
    checkBackgroundThread()
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .removeValue()
  }

  fun eraseBy(userToken: String, companyId: Int) {
    checkBackgroundThread()
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .child(companyId.toString())
      .removeValue()
  }
}

internal data class FavouriteCompanyResponse(
  val data: List<Int>
)

internal const val FAVOURITE_COMPANIES_REFERENCE = "favourite-companies"