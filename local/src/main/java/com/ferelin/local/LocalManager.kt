package com.ferelin.local

import android.content.Context
import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LocalManager(
    private val mJsonManagerHelper: JsonManagerHelper,
    private val mCompaniesManagerHelper: CompaniesManagerHelper
) : LocalManagerHelper {

    override fun getData(context: Context): Flow<List<Company>> = flow {
        mCompaniesManagerHelper.getAll().first { databaseList ->
            when {
                databaseList.isEmpty() -> {
                    mJsonManagerHelper.getData(context).first {
                        mCompaniesManagerHelper.insertAll(it)
                        emit(it)
                        true
                    }
                }
                else -> emit(databaseList)
            }
            true
        }
    }

    override fun insertData(data: List<Company>) {
        mCompaniesManagerHelper.insertAll(data)
    }

    override fun insert(company: Company) {
        mCompaniesManagerHelper.insert(company)
    }
}