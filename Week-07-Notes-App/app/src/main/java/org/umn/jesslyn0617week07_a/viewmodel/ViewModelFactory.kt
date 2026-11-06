package org.umn.jesslyn0617week07_a.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.umn.jesslyn0617week07_a.data.repository.NoteRepository
import kotlin.jvm.java

class ViewModelFactory(private val repository: NoteRepository):
    ViewModelProvider.Factory {
        override fun<T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                return NoteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
}