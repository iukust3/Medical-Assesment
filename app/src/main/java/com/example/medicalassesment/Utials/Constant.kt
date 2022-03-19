package com.example.medicalassesment.Utials

import com.example.medicalassesment.R
import com.example.medicalassesment.models.PreliminaryInfoModel
import com.example.medicalassesment.models.TemplateModel

class Constant {
    companion object {
        const val QUESTION_TYPE_YESNO = "1"
        const val QUESTION_TYPE_YESNO_WITH_COMMENT = "2"
        const val QUESTION_TYPE_TEXTAREA = "3"
        const val QUESTION_TYPE_EDITTEXT = "7"
        const val QUSTION_TYPE_SIGNATURE = "5"
        const val QUSTION_TYPE_DROPDOWN = "6"
        const val QUSTION_TYPE_DATEPAST = "8"
        const val QUSTION_TYPE_DATEFUTURE = "9"
        const val QUSTION_TYPE_PHONE = "10"
        const val QUSTION_TYPE_EMAIL = "11"
        const val QUSTION_TYPE_PROXIMITY = "12"
        const val QUSTION_TYPE_SECTIONTITTLE = "13"
        const val QUSTION_TYPE_MULTICHICE = "14"
        const val QUSTION_TYPE_READONLY = "15"
        const val HARD_DEAL_BREKER = "hard"
        const val SOFT_DEAL_BREKER = "soft"
        const val NORMAL_DEAL_BREKER = "normal"

        //Template Type
        const val TEMPLETE_TYPE_LOCATION = "1"
        const val TEMPLETE_TYPE_FECILITY = "2"
        const val TEMPLETE_TYPE_ROUTEN = "3"
        @JvmStatic
        fun getType(type: String): String {
            return when (type) {
                "1" -> "Location Inspection"
                "2" -> "Registration Inspection"
                "3" -> "Routine Inspection"
                else -> "Monitoring inspection"
            }
        }

        @Volatile
        @JvmStatic
        private var list: List<PreliminaryInfoModel> = ArrayList()

        /* COMMON TO ALL TEMPLATES - This should now appear on the first page
         Facility Name:
         Coordinates: (Longitude and Latitude)
         Address:
         Ward:
         LGA:
         State:

         FACILITY DETAILS


         Facility Category
         Facility Name:
         Coordinates: (Longitude and Latitude)
         Address:
         State:
         LGA:
         Ward:*/
        fun getDefaultQuestions(templateModel: TemplateModel): List<PreliminaryInfoModel> {
            list = ArrayList()
            var preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -7
            preliminaryInfoModel.title = "INSPECTION TOKEN  :"
            preliminaryInfoModel.question_type_id = QUESTION_TYPE_EDITTEXT
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)

            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -1
            preliminaryInfoModel.title = "Facility Name:"
            preliminaryInfoModel.question_type_id = QUESTION_TYPE_EDITTEXT
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)


            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -2
            preliminaryInfoModel.title = "Coordinates: (Latitude and Longitude)"
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.question_type_id = QUESTION_TYPE_EDITTEXT
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)

            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -3
            preliminaryInfoModel.title = "Address : "
            preliminaryInfoModel.question_type_id = QUESTION_TYPE_TEXTAREA
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)


            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -5
            preliminaryInfoModel.title = "State : "
            preliminaryInfoModel.question_type_id = QUSTION_TYPE_DROPDOWN
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)

            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -6
            preliminaryInfoModel.title = "LGA : "
            preliminaryInfoModel.question_type_id = QUSTION_TYPE_DROPDOWN
            preliminaryInfoModel.setPriority("1")
            preliminaryInfoModel.questiontype = "default"
            list = list.plus(preliminaryInfoModel)

            preliminaryInfoModel = PreliminaryInfoModel()
            preliminaryInfoModel.preliminery_info_id = -4
            preliminaryInfoModel.title = "Ward :"
            preliminaryInfoModel.question_type_id = QUSTION_TYPE_DROPDOWN
            preliminaryInfoModel.setPriority("0")
            preliminaryInfoModel.questiontype = "default"
            list= list.plus(preliminaryInfoModel)

            /* preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -7
                 preliminaryInfoModel.title = "FACILITY DETAILS"
                 preliminaryInfoModel.type = QUSTION_TYPE_SECTIONTITTLE
                 preliminaryInfoModel.setPriority("0")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -8
                 preliminaryInfoModel.title = "Facility Category:"
                 preliminaryInfoModel.type = QUSTION_TYPE_DROPDOWN
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -9
                 preliminaryInfoModel.title = "Facility Name:"
                 preliminaryInfoModel.type = QUSTION_TYPE_DROPDOWN
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -10
                 preliminaryInfoModel.title = "Coordinates: (Latitude and Longitude)"
                 preliminaryInfoModel.type = QUESTION_TYPE_EDITTEXT
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -11
                 preliminaryInfoModel.title = "Address : "
                 preliminaryInfoModel.type = QUESTION_TYPE_TEXTAREA
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -12
                 preliminaryInfoModel.title = "Ward :"
                 preliminaryInfoModel.type = QUESTION_TYPE_EDITTEXT
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -13
                 preliminaryInfoModel.title = "State : "
                 preliminaryInfoModel.type = QUSTION_TYPE_DROPDOWN
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)

                 preliminaryInfoModel = PreliminaryInfoModel()
                 preliminaryInfoModel.preliminery_info_id = -14
                 preliminaryInfoModel.title = "LGA : "
                 preliminaryInfoModel.type = QUSTION_TYPE_DROPDOWN
                 preliminaryInfoModel.setPriority("1")
                 preliminaryInfoModel.questiontype = "default"
                 list = list.plus(preliminaryInfoModel)*/

            return list
        }

        @JvmStatic
        fun getIcon(name: String): Int {
            return when (name) {
                "Hospital Pharmacy" ->
                    R.drawable.ic_hospital_pharmacy
                "Community Pharmacy" -> R.drawable.ic_community_pharmacy
                "Drug Manufacturing" -> R.drawable.ic_drug_manufacturing
                "Wholesale Centres" -> R.drawable.ic_drug_distribution
                "Scientific Office" -> R.drawable.ic_community_pharmacy
                else -> R.drawable.ic_ppmv
            }
        }
    }
}