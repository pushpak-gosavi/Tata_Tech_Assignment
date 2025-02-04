package com.pushpak.tatatechassignment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pushpak.tatatechassignment.model.Contact

class ContactViewModel: ViewModel() {
    var contacts by mutableStateOf(emptyList<Contact>())
        private set

    fun updateContacts(contacts: List<Contact>){
        this.contacts = contacts
    }
}