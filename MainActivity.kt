package com.securitypeople.todoapp

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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
import junit.framework.Test
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger




class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    var mInput:String = ""
    //lateinit var adapter: ArrayAdapter<*>
    var list:MutableList<ToDo> = ArrayList()
    //var recdata:String
    //var toDo: ToDo
    private var recyclerView: RecyclerView?=null
    //private var adapter: ToDoAdapter? = null
    private var compositeDisposable: CompositeDisposable?=null
    private var taskRepository:TaskRepository?=null
    val adapter = ToDoAdapter(this,list)
    private var btn_delete: Button?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_todo_list)

        //val recyclerView =  RecyclerView(this)
        var bundle = Bundle()
        var value = bundle.getString("param1")

        recyclerView =findViewById<View>(R.id.rvTodoList) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL, false)
        recyclerView!!.adapter = adapter
        recyclerView!!.addItemDecoration(DividerItemDecoration(recyclerView!!.getContext(), DividerItemDecoration.VERTICAL))

        //1.INITIALize
        compositeDisposable= CompositeDisposable()
        //adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
        registerForContextMenu(recyclerView)
        //lst_users!!.adapter = adapter
        //2.Database
        val taskdatabase = AppDatabase.getInstance(this)
        taskRepository = TaskRepository.getInstance(TaskDataSource.getInstance(taskdatabase.toDoDao()))
        //Load all data from Db
        loadData()
        //insert on Floating button clicked
        //val strtext = getArguments().getString("edttext")

        var strtext = bundle.getString("edttext")
        Logger.getLogger(Test::class.java.name).warning("Bundle in main"+strtext)
        //val toDoDelete = list[]

        fab_add.setOnClickListener({
            openDialog()
        })
        /*btn_delete!!.setOnClickListener({
            AlertDialog.Builder(this@MainActivity)
                    .setMessage("Are you sure you want to delete?")//toDo.todo
                    .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener
                    { dialog, which ->
                        deleteTask(toDoDelete)
                    })
                    .setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }.create().show()

        })*/


    }
    //open fragment ()->inserttask(String)->name from activity on reveive data
    private fun openDialog() {
        val inflater = LayoutInflater.from(this@MainActivity)
        val subView = inflater.inflate(R.layout.fragment_todo_dialog, null)
        val todotext = subView.findViewById<View>(R.id.todo) as EditText
        val datetext = subView.findViewById<View>(R.id.date)as EditText
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter your Task")
        //builder.setMessage("AlertDialog Message")
        builder.setView(subView)
        val alertDialog = builder.create()
        builder.setPositiveButton("OK", object:DialogInterface.OnClickListener{
            override fun onClick(dialog:DialogInterface, which:Int) {
                //textInfo.setText(subEditText.getText().toString())
                var todoTorec = todotext.getText().toString()+"-"+datetext.getText().toString()
                inserttask(todoTorec)

            }
        })
        builder.setNegativeButton("Cancel", object:DialogInterface.OnClickListener {
            override fun onClick(dialog:DialogInterface, which:Int) {
                Toast.makeText(this@MainActivity, "Cancel", Toast.LENGTH_LONG).show()
            }
        })
        builder.show()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun loadData(){
        val disposable = taskRepository!!.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({toDo->onGetAllTasksSuccess(toDo)}){
                    throwable-> Toast.makeText(this@MainActivity,""+throwable.message,Toast.LENGTH_SHORT).show()
                }
        compositeDisposable!!.add(disposable)
    }
    private fun onGetAllTasksSuccess(toDo_List: List<ToDo>){
        list.clear()
        list.addAll(toDo_List)
        /*for(item in toDo_List)
        {
            var toDoText:String?="" //var is mutable
            var createdAt:String?=""

            if((item.todoText!= null)&&(item.createdAt!=null)){
                toDoText= item.todoText
                createdAt = item.todoText
            }
            list.add(toDoText+"  "+createdAt)}*/

        adapter!!.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteall->deleteAllTask()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun deleteAllTask(){
        val disposable = Observable.create(ObservableOnSubscribe<Any>{
            e->taskRepository!!.deleteAll()
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer{

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }

    /*override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        menu.setHeaderTitle("Select")
        //menu.add(Menu.NONE,1,Menu.NONE,"UPDATE")
        menu.add(Menu.NONE,0,Menu.NONE,"Delete")

    }*/

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo

        //val position = -1
        //position = (recyclerView!!.getAdapter() as BackupRestoreListAdapter).getPosition()
        //val position =(list.getAdapter() as ToDoAdapter).getPosition()
        val position = adapter.getPosition()
        val toDoDelete = list[position]
        when (item.itemId){
            0->{
                AlertDialog.Builder(this@MainActivity)
                        .setMessage("Are you sure you want to delete?")//toDo.todo
                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener
                        { dialog, which ->
                            deleteTask(toDoDelete)
                        })
                        .setNegativeButton(android.R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
            }

        }
        return true
    }
    private fun deleteTask(toDo: ToDo){
        val disposable = Observable.create(ObservableOnSubscribe<Any>{
            e->taskRepository!!.delete(toDo)
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer{

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)


    }
    private fun openfragment(){
        val ft = getSupportFragmentManager().beginTransaction()
        //val newFragment = Fragment_ToDoNow.newInstance()
        //newFragment.show(ft, "dialog")
        var todoTorec = receiveData()
        //var bundle = Bundle()
        //var value = bundle.getString("getdata")
        //var todoTorec = value
        //var todoTorec  = receiveData()
        //var name: String
        //val extras = getIntent().getExtras()

        //name = extras.getString("NAME_KEY")
        //var todoTorec = name

        //Logger.getLogger(Test::class.java.name).warning("Receive  "+name)
        //var todoTorec = bundle.getString("request");
        Logger.getLogger(Test::class.java.name).warning("abc  "+todoTorec)
        if(todoTorec != null){
            inserttask(todoTorec)}
        else {
            Toast.makeText(this,"couldnt insert",Toast.LENGTH_LONG).show()

        }

    }
    private fun inserttask(todoget :String ){
        val disposable = Observable.create(ObservableOnSubscribe<Any>{e->
            val todo = ToDo()

            todo.todoText = todoget
            val splitted_todo = todoget.split("-")
            todo.todoText = splitted_todo[0]
            todo.createdAt = splitted_todo[1]
            //todo = todoText
            //todo.todoText = "vbn"
            //todo.createdAt = "19th september"

            taskRepository!!.insert(todo)//imp
            e.onComplete()
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer{

                },
                        io.reactivex.functions.Consumer { throwable ->
                            Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }





    private  fun receiveData():String{
        //val i = getIntent()
        //val name = i.getStringExtra("NAME_KEY")
        //return name
        var name: String
        val extras = getIntent().getExtras()
        //if(extras!= null) {
        name = extras.getString("NAME_KEY")
        //}
        //else{
        //    Toast.makeText(this,"couldnt insert",Toast.LENGTH_LONG)
        //    name = null.toString()

        // }
        Logger.getLogger(Test::class.java.name).warning("Receive  "+name)
        return name

    }
    override fun onBackPressed() {
        //super.onBackPressed();
    }





}
