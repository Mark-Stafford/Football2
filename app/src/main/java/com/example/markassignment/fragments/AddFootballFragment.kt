package com.example.markassignment.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.markassignment.R
import com.example.markassignment.main.FootballApp
import com.example.markassignment.models.FootballModel
import com.example.markassignment.utils.createLoader
import com.example.markassignment.utils.hideLoader
import com.example.markassignment.utils.showLoader

import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.fragment_addfball.*
import kotlinx.android.synthetic.main.fragment_addfball.view.*

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.HashMap


class AddFootballFragment : Fragment(), AnkoLogger {

    lateinit var app: FootballApp
    lateinit var loader : AlertDialog
    var ball = FootballModel()
    lateinit var eventListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FootballApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_addfball, container, false)
        loader = createLoader(activity!!)
        activity?.title = getString(R.string.action_addfball)

        setButtonListener(root)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddFootballFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener( layout: View) {
        layout.buttonBallAdd.setOnClickListener {
            ball.ballname =
                ballName.text.toString()
            ball.balldescription =
                ballPosition.text.toString()
            ball.ballcountry =
                ballCountry.text.toString()

            writeNewFootball(FootballModel(ballname = ball.ballname, balldescription = ball.balldescription,
            ballcountry = ball.ballcountry))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        app.database.child("user-balls")
            .child(app.auth.currentUser!!.uid)
//           .removeEventListener(eventListener)
    }

    fun writeNewFootball(football: FootballModel) {

        showLoader(loader, "Adding ball to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("balls").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        football.uid = key
        val footballValues = football.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/balls/$key"] = footballValues
        childUpdates["/user-balls/$uid/$key"] = footballValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }




}
