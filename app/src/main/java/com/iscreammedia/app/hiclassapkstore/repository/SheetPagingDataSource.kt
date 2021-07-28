package com.iscreammedia.app.hiclassapkstore.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.iscreammedia.app.hiclassapkstore.model.SheetDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SheetPagingDataSource(
    private val service: Sheets
) : PagingSource<Int, SheetDataModel>() {

    companion object {
        private const val SIZE = 20
        private const val ROW_START_INDEX = 1
    }
    override fun getRefreshKey(state: PagingState<Int, SheetDataModel>): Int? {
        return ROW_START_INDEX
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SheetDataModel> {
        return try {
            var startIndex: Int? = params.key ?: ROW_START_INDEX
            val endIndex = (startIndex ?: ROW_START_INDEX) + SIZE

            val rows = withContext(Dispatchers.IO) {
                return@withContext service.spreadsheets()
                    .values()
                    .batchGetByDataFilter("1VyPnDKiEiHqZA6UoYm44xntmkFewfQzRpqbOM_My2e0",
                        BatchGetValuesByDataFilterRequest()
                            .apply {
                                dataFilters = listOf(DataFilter().apply {
                                    gridRange = GridRange()
                                        .setSheetId(243833115)
                                        .setStartRowIndex(startIndex)
                                        .setEndRowIndex(endIndex)
                                })
                            })
                    .execute()
                    .valueRanges[0]
                    .valueRange
                    .values
                    .toMutableList()
            }
            val convert = (rows[2] as List<List<String>>)
            startIndex = if (convert.size != SIZE) null
            else (startIndex ?: ROW_START_INDEX) + SIZE
            LoadResult.Page(
                data = convert.map {
                    SheetDataModel(
                        createdAt = it[0],
                        version = it[1],
                        content = " - ${it[2].replace("-","").replace("\n", "\n - ")}",
                        downloadUrl = it[4]
                    )
                },
                nextKey = startIndex,
                prevKey = null
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}