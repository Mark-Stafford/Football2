package com.example.markassignment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.markassignment.R
import com.example.markassignment.adapters.FootballAdapter
import com.example.markassignment.adapters.FootballListener
import com.example.markassignment.models.FootballModel
import com.example.markassignment.utils.createLoader
import com.example.markassignment.utils.hideLoader
import com.example.markassignment.utils.showLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_ball.view.*



import org.jetbrains.anko.info

class BallAllFragment : BallFragment(),
    FootballListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_ball, container, false)
        activity?.title = getString(R.string.menu_ball_all)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BallAllFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllUsersFootballs()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getAllUsersFootballs()
    }

    fun getAllUsersFootballs() {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading All Users Balls from Firebase")
        val ballsList = ArrayList<FootballModel>()
        app.database.child("balls")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Ball error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val donation = it.
                        getValue<FootballModel>(FootballModel::class.java)

                        ballsList.add(donation!!)
                        root.recyclerView.adapter =
                            FootballAdapter(ballsList, this@BallAllFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("balls").removeEventListener(this)
                    }
                }
            })
    }
}