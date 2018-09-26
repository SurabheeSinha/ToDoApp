package com.securitypeople.todoapp.database.dao

import android.arch.persistence.room.*
import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Flowable

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): Flowable<List<ToDo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todo: ToDo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg todo: ToDo)

    @Delete
    fun delete(vararg todo: ToDo)

    @Query("DELETE FROM todo")
    fun deleteAll()

}