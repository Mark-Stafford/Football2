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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: FootballApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editFootball: FootballModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FootballApp

        arguments?.let {
            editFootball = it.getParcelable("editfootball")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editName.setText(editFootball!!.ballname)
        root.editDescription.setText(editFootball!!.balldescription)
        root.editCountry.setText(editFootball!!.ballcountry)



        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating ball on Server...")
            updateFootballData()
            updateFootball(editFootball!!.uid, editFootball!!)
            updateUserFootball(app.auth.currentUser!!.uid,
                               editFootball!!.uid, editFootball!!)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(fball: FootballModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editfootball",fball)
                }
            }
    }

    fun updateFootballData() {
        editFootball!!.ballname = root.editName.text.toString()
        editFootball!!.balldescription = root.editDescription.text.toString()
        editFootball!!.ballcountry = root.editCountry.text.toString()
    }

    fun updateUserFootball(userId: String, wid: String?, fball: FootballModel) {
        app.database.child("user-balls").child(userId).child(wid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(fball)
                        activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFrame, BallFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase ball error : ${error.message}")
                    }
                })
    }

    fun updateFootball(wid: String?, fball: FootballModel) {
        app.database.child("stocks").child(wid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(fball)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase ball error : ${error.message}")
                    }
                })
    }
}
