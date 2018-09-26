package com.securitypeople.todoapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.securitypeople.todoapp.database.dao.ToDoDao
import com.securitypeople.todoapp.models.ToDo

@Database(entities = ([ToDo::class]), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

    companion object{

        const val DATABASE_VERSION = 1
        val DATABASE_NAME = "Todo"
        private var mInstance :AppDatabase?=null
        fun getInstance(context: Context):AppDatabase{
            if(mInstance == null)
                mInstance= Room.databaseBuilder(context,AppDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
            return mInstance!!
        }

    }

}