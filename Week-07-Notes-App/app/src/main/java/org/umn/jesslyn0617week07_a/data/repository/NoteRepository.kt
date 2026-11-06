package org.umn.jesslyn0617week07_a.data.repository

import kotlinx.coroutines.flow.Flow
import org.umn.jesslyn0617week07_a.data.local.NoteDao
import org.umn.jesslyn0617week07_a.model.Note

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun insert(note: Note) {
        noteDao.insertNote(note)
    }
    suspend fun delete(note: Note) {
        noteDao.deleteNote(note)
    }
}