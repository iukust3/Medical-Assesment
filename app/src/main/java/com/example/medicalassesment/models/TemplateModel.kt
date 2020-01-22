package com.example.medicalassesment.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Utials.Utils

@Entity
class TemplateModel() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var description: String? = ""
    var templateId = 0
    var isUploaded = false
    var status: String? = "InProgress"
    var title: String? = ""
    var category: String? = ""
    var inspectionConductedOn: String? = Utils.getFormattedDateSimple()
    var inspectionConductedBy: String? =
        MyApplication.USER.firstname + " " + MyApplication.USER.surname
    var inspectionConductedAt: String? = ""
    var type:String?=""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        description = parcel.readString()
        templateId = parcel.readInt()
        isUploaded = parcel.readByte() != 0.toByte()
        status = parcel.readString()
        title = parcel.readString()
        category = parcel.readString()
        type = parcel.readString()
        inspectionConductedOn = parcel.readString()
        inspectionConductedBy = parcel.readString()
        inspectionConductedAt = parcel.readString()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeInt(templateId)
        parcel.writeByte(if (isUploaded) 1 else 0)
        parcel.writeString(status)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(type)
        parcel.writeString(inspectionConductedOn)
        parcel.writeString(inspectionConductedBy)
        parcel.writeString(inspectionConductedAt)
    }

    companion object CREATOR : Parcelable.Creator<TemplateModel> {
        override fun createFromParcel(parcel: Parcel): TemplateModel {
            return TemplateModel(parcel)
        }

        override fun newArray(size: Int): Array<TemplateModel?> {
            return arrayOfNulls(size)
        }
    }

}