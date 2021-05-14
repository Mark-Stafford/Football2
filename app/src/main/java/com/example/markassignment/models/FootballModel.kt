package com.example.markassignment.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties

@Parcelize
data class FootballModel(var uid: String = "",
                         var ballname: String = "",
                         var balldescription: String = "",
                         var ballcountry:String= "")
                        : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "ballname" to ballname,
            "balldescription" to balldescription,
            "ballcountry" to ballcountry
        )
    }
}


