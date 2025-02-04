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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.pushpak.tatatechassignment.utils.constants.COLUMN_NAME
import com.pushpak.tatatechassignment.utils.constants.DATA_URI
import com.pushpak.tatatechassignment.utils.constants.ID

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

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
                Scaffold { padding ->
                    Getdata(padding)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactList() {
        val contentResolver = applicationContext.contentResolver
        /*val projection = arrayOf(
            ID, COLUMN_NAME
        )*/
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
                    contacts.add(IAVContentData(id = id, data.toString()))
                }
                viewModel.updateIAVContentData(contacts)
            }

    }

    @Composable
    fun Getdata(padding: PaddingValues) {
        var number by remember { mutableStateOf("") }
        var randomString by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(100.dp))
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                placeholder = {
                    Text("Please Enter a Number")
                },
                label = {
                    Text("Please Enter a Number")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                if (number != "") {
                    randomString = getRandomString(number.toInt())
                    applicationContext.contentResolver.insertData(number.toInt())
                }
            }) {
                Text("Get Random String")
            }
            Spacer(Modifier.height(10.dp))
            Text(modifier = Modifier.padding(horizontal = 10.dp), text = randomString)

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .height(200.dp)
            ) {
                items(viewModel.iavContentData) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(10F)
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
                                applicationContext.contentResolver.deleteData(contact.id)
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


    private fun ContentResolver.insertData(length: Int) {
        try {
            val randomString = getRandomString(length = length)
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
            val deleteUri = Uri.parse("${DATA_URI}/$id")
            delete(deleteUri, null, null)
        } catch (ex: Exception) {
            Log.d("Delete_Exception", ex.toString())
        }
    }


    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}