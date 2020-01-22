package com.example.medicalassesment.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils

@Entity
data class Facility(
    @ColumnInfo val address: String = "",
    @ColumnInfo val category: String = "",
    @ColumnInfo val email: String = "",
    @PrimaryKey val id: Int = 0,
    @ColumnInfo val name: String = "",
    @ColumnInfo val phone: String = "",
    @ColumnInfo var dateOfLastInspection: String = ""
)