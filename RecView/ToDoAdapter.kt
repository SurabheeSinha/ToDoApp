package com.securitypeople.todoapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.securitypeople.todoapp.models.ToDo



/**
 * Created by surabheesinha on 9/21/18.
 */
class ToDoAdapter(internal var context: Context, internal var toDoList:List<ToDo>):RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>()   {

    //val itemCount:Int
    private var position:Int = 0
    fun getPosition():Int {
        return position
    }
    fun setPosition(position:Int) {
        this.position = position
    }


    override fun getItemCount(): Int {
        return toDoList.size
       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val itemv = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_list, parent, false)
        return ToDoViewHolder(itemv) }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.textTodo.text = toDoList[position].todoText
        holder.createdAt.text = toDoList[position].createdAt
        holder.itemView.setOnLongClickListener(object:View.OnLongClickListener {
            override fun onLongClick(v:View):Boolean {
                setPosition(holder.getLayoutPosition())
                //itemView.showContextMenu();
                return false
            }
        })
    }

    class ToDoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            //val info = menuInfo as AdapterView.AdapterContextMenuInfo
            //val position = (getAdapter() as BackupRestoreListAdapter).getPosition()
            menu!!.setHeaderTitle("Select")
            //menu.add(Menu.NONE,1,Menu.NONE,"UPDATE")
            menu!!.add(Menu.NONE,0, Menu.NONE,"Delete")
        }

        val textTodo = itemView.findViewById<TextView>(R.id.tvContext)as TextView
        val createdAt = itemView.findViewById<TextView>(R.id.tvDate)as TextView
        init{
            itemView.setOnCreateContextMenuListener(this)

            }


    }
}