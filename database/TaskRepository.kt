package com.securitypeople.todoapp.database

import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Flowable

/**
 * Created by surabheesinha on 9/19/18.
 */
class TaskRepository(private val msource: iTaskDataSource): iTaskDataSource{
    override fun getAll(): Flowable<List<ToDo>> {
        return msource.getAll()
       }

    override fun insert(vararg todotext: ToDo) {
       return msource.insert(*todotext)
          }

    override fun delete(vararg todo: ToDo) {
        return msource.delete(*todo)
          }

    override fun deleteAll(){
        return msource.deleteAll()
    }
    companion object {
        private var mInstance :TaskRepository?=null
        fun getInstance(msource: iTaskDataSource):TaskRepository{
            if(mInstance==null)
                mInstance = TaskRepository(msource)
            return mInstance!!
        }
    }

}