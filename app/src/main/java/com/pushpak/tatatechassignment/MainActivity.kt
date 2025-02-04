package com.pushpak.tatatechassignment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.pushpak.tatatechassignment.model.Contact
import com.pushpak.tatatechassignment.ui.theme.TataTechAssignmentTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ContactViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            0
        )
        getContactList()
        setContent {
            TataTechAssignmentTheme {
                Getdata()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactList() {
        val contentResolver = applicationContext.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
        )
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColum =
                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

            val contacts = mutableListOf<Contact>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColum)
                contacts.add(Contact(id, name))
            }
            viewModel.updateContacts(contacts)
        }

    }

    @Composable
    fun Getdata() {
        var number by remember { mutableStateOf("") }
        var randomString by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(100.dp))
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Left,
                text = "Please enter number in belows Edit Text for getting the random string from the existing application with specified length that you mentioning in Edit Text",
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                placeholder = {
                    Text("Please Enter a Number")
                },
                label = {
                    Text("Please Enter a Number")
                },
                textStyle = TextStyle(color = Color.Black),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                if (number != "")
                    randomString = "get Random Strinng"
            }) {
                Text("Get Random String")
            }
            Spacer(Modifier.height(10.dp))
            Text(text = randomString)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                items(viewModel.contacts) { contact ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = contact.name
                    )
                }
            }
        }
    }

    @Preview(showSystemUi = true, showBackground = true)
    @Composable
    fun PreviewGenerateData() {
        Getdata()
    }
}