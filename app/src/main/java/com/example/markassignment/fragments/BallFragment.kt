package com.example.markassignment.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.markassignment.R
import com.example.markassignment.adapters.FootballAdapter
import com.example.markassignment.adapters.FootballListener
import com.example.markassignment.main.FootballApp
import com.example.markassignment.models.FootballModel
import com.example.markassignment.utils.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_ball.view.*


import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

open class BallFragment : Fragment(), AnkoLogger,
    FootballListener {

    lateinit var app: FootballApp
    lateinit var loader : AlertDialog
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FootballApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_ball, container, false)
        activity?.title = getString(R.string.action_ball)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView.adapter as FootballAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteFootball((viewHolder.itemView.tag as FootballModel).uid)
                deleteUserFootball(app.auth.currentUser!!.uid,
                    (viewHolder.itemView.tag as FootballModel).uid)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onFootballClick(viewHolder.itemView.tag as FootballModel)
            }
        }

        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BallFragment().apply {
                arguments = Bundle().apply { }
            }
    }

  open fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllFootballs(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }


    fun deleteUserFootball(userId: String, wid: String?) {
        app.database.child("user-balls").child(userId).child(wid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Ball error : ${error.message}")
                    }
                })
    }


    fun deleteFootball(wid: String?) {
        app.database.child("balls").child(wid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Ball error : ${error.message}")
                    }
                })
    }


    override fun onFootballClick(football: FootballModel) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, EditFragment.newInstance(football))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        if(this::class == BallFragment::class)
            getAllFootballs(app.auth.currentUser!!.uid)
    }

    fun getAllFootballs(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading ball from Firebase")
        val footballList = ArrayList<FootballModel>()
        app.database.child("user-balls").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase ball error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val football = it.
                        getValue<FootballModel>(FootballModel::class.java)

                        footballList.add(football!!)
                        root.recyclerView.adapter =
                            FootballAdapter(footballList, this@BallFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("user-balls").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
    }
}
