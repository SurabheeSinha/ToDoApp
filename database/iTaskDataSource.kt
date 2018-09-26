package com.securitypeople.todoapp.database

import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Flowable

/**
 * Created by surabheesinha on 9/19/18.
 */
interface iTaskDataSource {
    //val allTask:Flowable<List<ToDo>>
    fun getAll():Flowable<List<ToDo>>
    fun insert(vararg toDo: ToDo)
    fun delete(vararg toDo: ToDo)
    fun deleteAll()
}