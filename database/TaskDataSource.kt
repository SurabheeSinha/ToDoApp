package com.securitypeople.todoapp.database

import com.securitypeople.todoapp.database.dao.ToDoDao
import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Flowable

/**
 * Created by surabheesinha on 9/19/18.
 */
class TaskDataSource(private val toDoDao:ToDoDao):iTaskDataSource {


    override fun getAll(): Flowable<List<ToDo>> {
        return toDoDao.getAll()
    }
    override fun insert(vararg toDo: ToDo) {
        return toDoDao.insert(*toDo)
    }
    override fun update(vararg toDo: ToDo) {
        return toDoDao.update(*toDo)
    }

    override fun delete(vararg toDo: ToDo) {
        return toDoDao.delete(*toDo)
    }
    override fun deleteAll(){
        return toDoDao.deleteAll()
    }
    companion object {
        private var mInstance: TaskDataSource? = null
        fun getInstance(toDoDao: ToDoDao): TaskDataSource {
            if (mInstance == null)
                mInstance = TaskDataSource(toDoDao)
            return mInstance!!
        }
    }
}