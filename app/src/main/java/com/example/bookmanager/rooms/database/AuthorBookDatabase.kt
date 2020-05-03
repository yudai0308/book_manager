package com.example.bookmanager.rooms.database

import androidx.room.Database
import com.example.bookmanager.rooms.entities.AuthorBook

@Database(entities = [AuthorBook::class], version = 1)
class AuthorBookDatabase {
}
