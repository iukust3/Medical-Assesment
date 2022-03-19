package com.example.medicalassesment.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class InspectionToolsItem(
    var name: String,
    var icon:Int,
    var list: ArrayList<Inspectiontype>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.createTypedArrayList(Inspectiontype)!!
    )

    constructor() : this("",-1, ArrayList())

    data class Inspectiontype(
        var name: String,
        var list: List<TemplateModel>
    ):Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.createTypedArrayList(TemplateModel)!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeTypedList(list)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Inspectiontype> {
            override fun createFromParcel(parcel: Parcel): Inspectiontype {
                return Inspectiontype(parcel)
            }

            override fun newArray(size: Int): Array<Inspectiontype?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeTypedList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InspectionToolsItem> {
        override fun createFromParcel(parcel: Parcel): InspectionToolsItem {
            return InspectionToolsItem(parcel)
        }

        override fun newArray(size: Int): Array<InspectionToolsItem?> {
            return arrayOfNulls(size)
        }
    }
}