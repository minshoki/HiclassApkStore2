package com.iscreammedia.app.hiclassapkstore.viewmodel

import androidx.lifecycle.ViewModel
import com.google.api.services.sheets.v4.Sheets
import com.iscreammedia.app.hiclassapkstore.repository.SheetRepository

class SheetViewModel(
    private val sheetRepository: SheetRepository
): ViewModel() {

    fun loadSheetData(service: Sheets) = sheetRepository.getSheetData(service)
}