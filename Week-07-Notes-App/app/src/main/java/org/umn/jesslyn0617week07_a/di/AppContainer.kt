package org.umn.jesslyn0617week07_a.di

import android.content.Context
import org.umn.jesslyn0617week07_a.data.local.NoteDatabase
import org.umn.jesslyn0617week07_a.data.repository.NoteRepository

class AppContainer (context: Context) {
    private val database: NoteDatabase by lazy {
        NoteDatabase.getDatabase(context)
    }
    val noteRepository: NoteRepository by lazy {
        NoteRepository(database.noteDao())
    }
}