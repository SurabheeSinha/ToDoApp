package com.securitypeople.todoapp.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "todo")
data class ToDo(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,


        @ColumnInfo(name = "todoText")
        var todoText: String = "",

        @ColumnInfo(name = "createdAt")
        var createdAt: String = ""


)











