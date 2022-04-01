package com.ferelin.core.data.entity.favouriteCompany

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal interface FavouriteCompanyApi {
  fun load(userToken: String): Flow<FavouriteCompanyResponse>
  fun putBy(userToken: String, companyId: Int)
  fun eraseAll(userToken: String)
  fun eraseBy(userToken: String, companyId: Int)
}

internal class FavouriteCompanyApiImpl(
  private val firebaseReference: DatabaseReference,
) : FavouriteCompanyApi {
  override fun load(userToken: String): Flow<FavouriteCompanyResponse> = callbackFlow {
    val valueEventListener = object : ValueEventListener {
      override fun onDataChange(resultSnapshot: DataSnapshot) {
        val response = FavouriteCompanyResponse(
          data = resultSnapshot.children.map { idSnapshot ->
            idSnapshot.key!!.toInt()
          }
        )
        trySend(response)
      }

      override fun onCancelled(error: DatabaseError) = Unit
    }

    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .addValueEventListener(valueEventListener)
    awaitClose { firebaseReference.removeEventListener(valueEventListener) }
  }

  override fun putBy(userToken: String, companyId: Int) {
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .child(companyId.toString())
      .setValue(companyId)
  }

  override fun eraseAll(userToken: String) {
    firebaseReference
      .child(FAVOURITE_COMPANIES_REFERENCE)
      .child(userToken)
      .removeValue()
  }

  override fun eraseBy(userToken: String, companyId: Int) {
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