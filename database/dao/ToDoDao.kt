package com.securitypeople.todoapp.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Flowable

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): Flowable<List<ToDo>>

    @Insert
    fun insert(vararg todo: ToDo)

    @Delete
    fun delete(vararg todo: ToDo)

    @Query("DELETE FROM todo")
    fun deleteAll()

}