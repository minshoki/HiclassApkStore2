package com.iscreammedia.app.hiclassapkstore.model

data class SheetDataModel(
    val version: String,
    val createdAt: String,
    val content: String,
    val downloadUrl: String
) {
    fun isDownload() = downloadUrl.isNotBlank()
}