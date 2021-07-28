package com.iscreammedia.app.hiclassapkstore

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.iscreammedia.app.hiclassapkstore.repository.SheetRepository
import com.iscreammedia.app.hiclassapkstore.viewmodel.SheetViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppModules {
    private val repository = module {
        factory { SheetRepository() }
    }
    private val viewModel = module {
        viewModel { SheetViewModel(sheetRepository = get()) }
    }
    private val etc = module {
    }
    val modules = listOf(repository, viewModel, etc)
}