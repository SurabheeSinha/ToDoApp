package com.securitypeople.todoapp

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.securitypeople.todoapp.RecView.ToDoAdapter
import com.securitypeople.todoapp.database.AppDatabase
import com.securitypeople.todoapp.database.TaskDataSource
import com.securitypeople.todoapp.database.TaskRepository
import com.securitypeople.todoapp.models.ToDo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_todo_list.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    var list: MutableList<ToDo> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var taskRepository: TaskRepository? = null
    val adapter = ToDoAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_todo_list)
        recyclerView = findViewById<View>(R.id.rvTodoList) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView!!.adapter = adapter
        recyclerView!!.addItemDecoration(DividerItemDecoration(recyclerView!!.getContext(), DividerItemDecoration.VERTICAL))
        //1.INITIALize
        compositeDisposable = CompositeDisposable()
        registerForContextMenu(recyclerView)
        //2.Database
        val taskdatabase = AppDatabase.getInstance(this)
        taskRepository = TaskRepository.getInstance(TaskDataSource.getInstance(taskdatabase.toDoDao()))
        //Load all data from Db
        loadData()
        //insert on Floating button clicked
        fab_add.setOnClickListener({
            openDialog()
        })
    }


    //opendialog()->inserttask(String)->insert(task)
    private fun openDialog() {
        val inflater = LayoutInflater.from(this@MainActivity)
        val subView = inflater.inflate(R.layout.fragment_todo_dialog, null)
        val todotext = subView.findViewById<View>(R.id.todo) as EditText
        val datetext = subView.findViewById<View>(R.id.date) as EditText
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter new Task")
        builder.setView(subView)
        builder.setPositiveButton("SAVE", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if ((todotext.getText().toString() == "") || (datetext.getText().toString() == "")) {
                    Toast.makeText(this@MainActivity, "Task/Date cannot be left Empty", Toast.LENGTH_LONG).show()
                    openDialog()
                } else {
                    var todoTorec = todotext.getText().toString() + "-" + datetext.getText().toString()
                    inserttask(todoTorec)
                    Toast.makeText(this@MainActivity, "Saved Successfuly", Toast.LENGTH_LONG).show()
                }
            }
        })
        builder.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                // Toast.makeText(this@MainActivity, "Cancel", Toast.LENGTH_Short).show()
            }
        })
        builder.show()
    }

    private fun loadData() {
        val disposable = taskRepository!!.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ toDo -> onGetAllTasksSuccess(toDo) }) { throwable ->
                    Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_SHORT).show()
                }
        compositeDisposable!!.add(disposable)
    }

    private fun onGetAllTasksSuccess(toDo_List: List<ToDo>) {
        list.clear()
        list.addAll(toDo_List)
        adapter!!.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteall -> deleteAllTask()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTask() {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            taskRepository!!.deleteAll()
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.getPosition()
        val toDoItem = list[position]
        when (item.itemId) {
            0 -> {
                AlertDialog.Builder(this@MainActivity)
                        .setMessage("Are you sure you want to delete?")//toDo.todo
                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener
                        { dialog, which ->
                            deleteTask(toDoItem)
                        })
                        .setNegativeButton(android.R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
            }
            1->{
                val editName = EditText(this@MainActivity)
                editName.setText(toDoItem.todoText)
                editName.hint = "Enter the Task"
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Edit")
                        .setMessage("Edit the Task")
                        .setView(editName)
                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener
                        { dialog, which ->
                           if(TextUtils.isEmpty(editName.text.toString()))
                            return@OnClickListener
                            else{
                               toDoItem.todoText = editName.text.toString()
                               updateTask(toDoItem)
                           }

                        })
                        .setNegativeButton(android.R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.create().show()

            }
        }
        return true
    }
    private fun updateTask(toDo: ToDo){
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            //val todo = ToDo()
            //todo.todoText = todoget
            //val splitted_todo = todoget.split("-")
            //todo.todoText = splitted_todo[0]
            //todo.createdAt = splitted_todo[1]
            //ntodo = todoText
            //ntodo.todoText = "Surabhee"
            //ntodo.createdAt = "19th september"
            taskRepository!!.update(toDo)
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)

    }

    private fun deleteTask(toDo: ToDo) {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            taskRepository!!.delete(toDo)
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)


    }

    private fun inserttask(todoget: String) {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            val todo = ToDo()
            todo.todoText = todoget
            val splitted_todo = todoget.split("-")
            todo.todoText = splitted_todo[0]
            todo.createdAt = splitted_todo[1]
            //ntodo = todoText
            //ntodo.todoText = "Surabhee"
            //ntodo.createdAt = "19th september"
            taskRepository!!.insert(todo)
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }


}
