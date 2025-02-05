package com.pushpak.tatatechassignment


import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pushpak.tatatechassignment.model.IAVContentData
import com.pushpak.tatatechassignment.model.RandomText
import com.pushpak.tatatechassignment.model.TableData
import com.pushpak.tatatechassignment.ui.theme.TataTechAssignmentTheme
import com.pushpak.tatatechassignment.utils.COLUMN_NAME
import com.pushpak.tatatechassignment.utils.DATA_URI
import com.pushpak.tatatechassignment.utils.ID

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getContactList()
        } catch (ex: Exception) {
            Log.d("exception_calling", ex.toString())
        }

        setContent {
            TataTechAssignmentTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Random String") },
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                ) { padding ->
                    Getdata(padding)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactList() {
        val contentResolver = applicationContext.contentResolver
        contentResolver.query(
            Uri.parse(DATA_URI),
            null,
            "column_name = ?",      // Selection (WHERE clause)
            arrayOf("value"),       // Selection arguments
            "column_name DESC"
        )
            ?.use { cursor ->
                val contacts = mutableListOf<IAVContentData>()
                while (cursor.moveToNext()) { //  Moves to the first row
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    contacts.add(IAVContentData(_id = id, data.toString()))
                }
                viewModel.updateIAVContentData(contacts)
            }

    }

    @Composable
    fun Getdata(padding: PaddingValues) {
        var number by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var randomString by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            OutlinedTextField(
                value = number,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length < 4) {
                        number = input
                        errorMessage = if (input.isEmpty()) "Please Enter Length" else null
                    } else if (input.length > 3) {
                        errorMessage = "Maximum 3 Digits Are Allowed"
                    } else
                        errorMessage = "Only Numbers Are Allowed"
                },
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                placeholder = {
                    Text("Please Enter Length")
                },
                label = {
                    Text("Please Enter Length")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                if (number.isEmpty())
                    errorMessage = "Please Enter Length"
                else {
                    randomString = viewModel.getRandomString(number.toInt())
                    applicationContext.contentResolver.insertData(number.toInt())
                }
            }) {
                Text("Get Random String")
            }
            Spacer(Modifier.height(10.dp))
            Text(modifier = Modifier.padding(horizontal = 10.dp), text = randomString)
            ShowDataView(viewModel = viewModel)
        }
    }


    private fun ContentResolver.insertData(length: Int) {
        try {
            val randomString = viewModel.getRandomString(length = length)
            val objRandomText = RandomText(created = randomString, length = length, value = "123")
            val objTable = TableData(objRandomText)
            val values = ContentValues().apply {
                put("data", objTable.toString())
            }
            insert(Uri.parse(DATA_URI), values)
        } catch (ex: Exception) {
            Log.d("Insert_Exception", ex.toString())
        }
    }

    private fun ContentResolver.deleteData(id: Int) {
        try {
            val deleteUri = Uri.parse("$DATA_URI/$id")
            delete(deleteUri, null, null)
        } catch (ex: Exception) {
            Log.d("Delete_Exception", ex.toString())
        }
    }

    @Composable
    fun ShowDataView(viewModel: MainViewModel) {
        LazyColumn(
            modifier = Modifier
                .height(500.dp)
        ) {
            items(viewModel.iavContentData) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(

                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(8F),
                        text = contact.data
                    )
                    IconButton(
                        modifier = Modifier.weight(2F),
                        onClick = {
                            applicationContext.contentResolver.deleteData(contact._id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,  // Using a predefined icon (Favorite)
                            contentDescription = "Favorite Icon",  // Accessibility description
                        )
                    }
                }
            }
        }
    }

}