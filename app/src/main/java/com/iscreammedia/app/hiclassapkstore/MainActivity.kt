package com.iscreammedia.app.hiclassapkstore

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.iscreammedia.app.hiclassapkstore.databinding.ActivityMainBinding
import com.iscreammedia.app.hiclassapkstore.viewmodel.SheetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*

class MainActivity : AppCompatActivity() {
    private val contractGoogleSignIn =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .addOnSuccessListener { account ->
                        val scopes =
                            listOf(SheetsScopes.SPREADSHEETS, SheetsScopes.SPREADSHEETS_READONLY)
                        val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
                        credential.selectedAccountName = account.account?.name
                        credential.selectedAccount = account.account

                        val jsonFactory = JacksonFactory.getDefaultInstance()
                        // GoogleNetHttpTransport.newTrustedTransport()
                        val httpTransport =
//                        GoogleNetHttpTransport.newTrustedTransport()
                            AndroidHttp.newCompatibleTransport()
                        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
                            .setApplicationName("HiclassApkStore")
                            .build()

                        createSpreadsheet(service)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }

    private val sheetViewModel: SheetViewModel by viewModel()
    private val sheetAdapter: SheetAdapter by lazy { SheetAdapter() }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rv.adapter = sheetAdapter

        binding.swipe.setOnRefreshListener {
            sheetAdapter.refresh()
        }


        sheetAdapter.addLoadStateListener {
            if(it.refresh != LoadState.Loading) {
                binding.swipe.isRefreshing = false
            }
        }
        requestSignIn()
    }

    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(
                Scope(SheetsScopes.SPREADSHEETS),
                Scope(SheetsScopes.SPREADSHEETS_READONLY)
            )
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        contractGoogleSignIn.launch(client.signInIntent)
    }

    private fun createSpreadsheet(service: Sheets) {
        lifecycleScope.launch {
            sheetViewModel.loadSheetData(service)
                .collect {
                    sheetAdapter.submitData(it)
                }
        }
    }
}