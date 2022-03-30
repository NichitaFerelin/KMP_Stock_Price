package com.ferelin.core.data.entity.favouriteCompany

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

internal interface FavouriteCompanyApi {
  fun load(userToken: String): Flowable<FavouriteCompanyResponse>
  fun putBy(userToken: String, companyId: Int)
  fun eraseAll(userToken: String)
  fun eraseBy(userToken: String, companyId: Int)
}

internal class FavouriteCompanyApiImpl @Inject constructor(
  private val firebaseReference: DatabaseReference,
) : FavouriteCompanyApi {
  private var valueEventListener: ValueEventListener? = null

  override fun load(
    userToken: String
  ): Flowable<FavouriteCompanyResponse> = Flowable.create<FavouriteCompanyResponse>(
    { emitter ->
      valueEventListener = object : ValueEventListener {
        override fun onDataChange(resultSnapshot: DataSnapshot) {
          val response = FavouriteCompanyResponse(
            data = resultSnapshot.children.map { idSnapshot ->
              idSnapshot.key!!.toInt()
            }
          )
          emitter.onNext(response)
        }

        override fun onCancelled(error: DatabaseError) = Unit
      }

      firebaseReference
        .child(FAVOURITE_COMPANIES_REFERENCE)
        .child(userToken)
        .addValueEventListener(valueEventListener!!)

    }, BackpressureStrategy.BUFFER
  )
    .doOnComplete {
      firebaseReference
        .child(FAVOURITE_COMPANIES_REFERENCE)
        .child(userToken)
        .removeEventListener(valueEventListener!!)
      valueEventListener = null
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