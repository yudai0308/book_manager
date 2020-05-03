package com.example.bookmanager.rooms.database

import androidx.room.Database
import com.example.bookmanager.rooms.entities.Author

@Database(entities = [Author::class], version = 1)
class AuthorDatabase {
}
