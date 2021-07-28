package com.iscreammedia.app.hiclassapkstore.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.api.services.sheets.v4.Sheets
import com.iscreammedia.app.hiclassapkstore.model.SheetDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SheetRepository() {

    fun getSheetData(service: Sheets): Flow<PagingData<SheetDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SheetPagingDataSource(service) }
        ).flow
    }
}