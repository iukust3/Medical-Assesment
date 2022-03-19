package com.example.medicalassesment.reportGenration

import android.content.Context
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.R
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.models.*
import com.itextpdf.text.*
import java.io.ByteArrayOutputStream
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Chunk
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import java.io.FilenameFilter
import java.text.DecimalFormat
import java.util.concurrent.Phaser
import kotlin.collections.ArrayList


class PdfCreater(val templateModel: TemplateModel, val context: Context) {

    val dao = MedicalDataBase.getInstance(context).getDao()
    val qustionList = dao.getQuestionsNonlive(templateModel.id.toString())

    companion object {
        private var stateindex = -1;
        private var lgaindex = -1;
    }

    init {
        try {
            createPdf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createPdf() {
        try {

            val path =
                Utils.createReportsFolder(context) + "/" + (templateModel.title?.split("/")?.get(0)
                    ?: "") + "-Report.pdf"
            var file = File(path)
            if (!file.exists())
                file.createNewFile()
            var font = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.BOLD, BaseColor.BLACK)

            var document = Document()
            var writer = PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()
            document.pageSize = PageSize.A4
            val d = ContextCompat.getDrawable(
                context,
                R.drawable.pcn
            )


            //region Add logo
            val bitDw = d as BitmapDrawable
            val bmp = bitDw.bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.scaleToFit(50f, 50f)
            val x = (PageSize.A4.width - image.scaledWidth) / 2
            val y = (PageSize.A4.height - (image.scaledHeight + 50))
            image.alignment = Element.ALIGN_CENTER
            document.add(image)
            //endregion

            //region Print Preliminary Report
            font = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.RED)

            var mOrderDetailsTitleChunk = Chunk(
                "Preliminary Report".toUpperCase(),
                font
            )
            var mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)
            //endregion


            //region Print Inspection Name
            font = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK)
            mOrderDetailsTitleChunk = Chunk(
                templateModel.description,
                font
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)
            //endregion


            //region Print Facility Name
            mOrderDetailsTitleChunk = Chunk(
                templateModel.title,
                font
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)
            //endregion

            //Add score
            addScore(document)

            drawPrelimanryInfo(document)
            drawQustions(document)
            drawFeedBack(document)
            document.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkRecommendation(table: PdfPTable) {
        val list = dao.getFaildItems(templateModel.id.toString(), Constant.HARD_DEAL_BREKER)
        val listsoft = dao.getFaildItems(templateModel.id.toString(), Constant.SOFT_DEAL_BREKER)
        val listNormal = dao.getFaildItems(templateModel.id.toString(), Constant.NORMAL_DEAL_BREKER)
        var celll: PdfPCell = PdfPCell()
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK)
        var paragraph = Paragraph()
        paragraph.add(
            Phrase(
                "Preliminary Recommendation\n".toUpperCase(),
                titleFont
            )
        )
        when {
            list.isNotEmpty() -> {
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.RED)
                paragraph.add(
                    Phrase(
                        "No Recommendation".toUpperCase(),
                        titleFont
                    )
                )
                celll = PdfPCell(
                    paragraph
                )
            }
            listsoft.isNotEmpty()
            -> {
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.YELLOW)
                paragraph.add(
                    Phrase(
                        "Provisional Recommendation".toUpperCase(),
                        titleFont
                    )
                )
                celll = PdfPCell(
                    paragraph
                )
            }
            listNormal.isNotEmpty()
            -> {
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.GREEN)
                paragraph.add(
                    Phrase(
                        "Full Recommendation".toUpperCase(),
                        titleFont
                    )
                )
                celll = PdfPCell(
                    paragraph
                )
            }
            else -> {
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.GREEN)
                paragraph.add(
                    Phrase(
                        "Full Recommendation".toUpperCase(),
                        titleFont
                    )
                )
                celll = PdfPCell(
                    paragraph
                )
            }
        }
        celll.verticalAlignment = Element.ALIGN_CENTER
        celll.horizontalAlignment = Element.ALIGN_CENTER
        celll.setPadding(5f)
        // celll.paddingTop=5f
        // celll.paddingBottom=2f
        table.addCell(celll)

    }

    private fun drawFailItems(document: Document) {
        addBreak(document)
        val list = dao.getFaildItems(templateModel.id.toString())
        var table = PdfPTable(1)
        table.defaultCell.border = Rectangle.BOTTOM
        table.widthPercentage = 100f
        table.defaultCell.setPadding(5f)
        table.addCell(Phrase("Fail Items : " + list.size))
        document.add(table)
        addBreak(document)
        val pointColumnWidths = floatArrayOf(8f, 2f)
        table = PdfPTable(pointColumnWidths)
        table.widthPercentage = 100f
        table.defaultCell.setPadding(5f)
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.WHITE)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)

        list.forEach {
            table.addCell(
                Phrase(
                    it.title,
                    titleFont
                )
            )
            var cell = PdfPCell(
                Phrase(
                    if (it.getAnswer() == "1") "YES" else "NO",
                    valueFont
                )
            )
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = BaseColor.RED
            table.addCell(
                cell
            )
        }
        document.add(table)
    }

    private fun addScore(document: Document) {
        var correct = 0f;
        var fail = 0;
        qustionList.forEach {
            if (it.getAnswer() != it.fail)
                correct++;
            else fail++;
        }
        val list = dao.getPrelimanryInfoNonlive(templateModel.id.toString())
        var table = PdfPTable(3)
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK)

        table.addCell(
            Phrase(
                "Score \n ${DecimalFormat("##.00").format((correct / qustionList.size) * 100)}%",
                titleFont
            )
        )
        table.addCell(
            Phrase(
                "fail \n $fail",
                titleFont
            )
        )
        checkRecommendation(table)
        table.getRow(0).cells[0].setPadding(5f)
        table.getRow(0).cells[0].borderColorLeft = BaseColor.WHITE
        table.getRow(0).cells[1].setPadding(5f)
        table.widthPercentage = 100f

        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        var mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        var mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.add(mOrderDetailsTitleParagraph)
        document.add(table)
        table = PdfPTable(2)
        table.defaultCell.setPadding(5f)
        table.widthPercentage = 100f
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor(153, 153, 153))

        mOrderDetailsTitleChunk = Chunk(
            "Time taken to complete inspection: ",
            valueFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleChunk = Chunk(
            " " + PrefHelper(context).getTime(templateModel.title.toString()),
            titleFont
        )
        mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        table.addCell(mOrderDetailsTitleParagraph)

        mOrderDetailsTitleChunk = Chunk(
            "Inspection Conducted On",
            valueFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleChunk = Chunk(
            " " + templateModel.inspectionConductedOn,
            titleFont
        )
        mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        table.addCell(mOrderDetailsTitleParagraph)

        mOrderDetailsTitleChunk = Chunk(
            "Inspection Conducted By",
            valueFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleChunk = Chunk(
            " " + templateModel.inspectionConductedBy,
            titleFont
        )
        mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        table.addCell(mOrderDetailsTitleParagraph)

        mOrderDetailsTitleChunk = Chunk(
            "Inspection Conducted At",
            valueFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleChunk = Chunk(
            " " + templateModel.inspectionConductedAt,
            titleFont
        )
        mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        table.addCell(mOrderDetailsTitleParagraph)
        document.add(table)

    }

    private fun drawPrelimanryInfo(document: Document) {
        var table = PdfPTable(1)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)

        table.defaultCell.border = Rectangle.BOTTOM
        table.widthPercentage = 100f
        // table.defaultCell.setPadding(5f)
        table.addCell(Phrase("Preliminary Information ".toUpperCase(), titleFont))
        document.add(table)
        table = PdfPTable(2)
        titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)

        var mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor(153, 153, 153))
        var mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER

        table.widthPercentage = 100f
        mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.add(mOrderDetailsTitleParagraph)
        table.defaultCell.setPadding(5f)
        val list = dao.getPrelimanryInfoNonlive(templateModel.id.toString())

        list.forEach {
            mOrderDetailsTitleChunk = Chunk(
                it.title,
                valueFont
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleChunk = if (it.question_type_id == Constant.QUSTION_TYPE_DROPDOWN) {
                try {
                    Chunk(
                        " " + (it.getDropDownItems()
                            ?.get(Integer.parseInt(it.getAnswer().toString()))
                            ?: ""),
                        titleFont
                    )
                } catch (e: Exception) {

                    val prefHelper = PrefHelper(context);
                    var answerLable = ""
                    try {

                        when (it.title) {

                            "Facility Name:" ->
                                answerLable =
                                    templateModel.title?.split("-")?.get(0)!!
                            "Ward :" -> {
                                answerLable = if (it.getAnswer().isNullOrEmpty()) {
                                    " "
                                } else
                                    prefHelper.getState().state[stateindex].lgas[lgaindex].wards[Integer.parseInt(
                                        it.getAnswer().toString()
                                    )].name
                            }
                            "State : " -> {
                                stateindex = Integer.parseInt(it.getAnswer().toString())
                                answerLable =
                                    prefHelper.getState()
                                        .getName(Integer.parseInt(it.getAnswer().toString()))

                            }
                            "LGA : " -> {
                                lgaindex = Integer.parseInt(it.getAnswer().toString())
                                answerLable =
                                    prefHelper.getState().state[stateindex].lgas[lgaindex].name
                            }
                            else -> {
                                answerLable = it.getAnswer()!!;
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    Chunk(
                        " $answerLable",
                        titleFont
                    )
                }
            } else
                Chunk(
                    " " + it.getAnswer(),
                    titleFont
                )
            mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            if (it.question_type_id == Constant.QUSTION_TYPE_SECTIONTITTLE) {
                mOrderDetailsTitleChunk = Chunk(
                    it.title,
                    titleFont
                )
                mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
                /* var alerdy = false;
                 if (table.defaultCell.colspan != 2) {
                     table.defaultCell.colspan = 2
                 } else {
                     alerdy = true;
                 }*/

                table.widthPercentage = 100f
                var cell = PdfPCell(
                    mOrderDetailsTitleParagraph
                )
                cell.backgroundColor = BaseColor(217, 217, 217)
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                cell.colspan = 2
                cell.setPadding(5f)
                //  table.defaultCell.backgroundColor=BaseColor(217, 217, 217)
                table.addCell(cell)

                /* if (!alerdy) {
                     table.defaultCell.colspan = 1
                 }*/
            } else {
                table.widthPercentage = 100f
                if (it.title?.contains("Ward :")!!) {
                    table.defaultCell.colspan = 2
                }
                table.addCell(mOrderDetailsTitleParagraph)
            }
            if (!it.imageUris.isNullOrEmpty())
                drawImages(it, table.getRow(table.rows.size - 1).cells[0], it.imageUris)

        }
        document.add(table)
    }

    private fun addBreak(document: Document) {
        val mOrderDetailsTitleChunk = Chunk(
            "   "
        )
        val mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.add(mOrderDetailsTitleParagraph)
    }

    private fun addBreak(document: PdfPCell) {
        val mOrderDetailsTitleChunk = Chunk(
            "   "
        )
        val mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.addElement(mOrderDetailsTitleParagraph)
    }

    private fun drawQustions(document: Document) {
        //document.newPage()

        var table = PdfPTable(1)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)
        var sectionFont = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.BOLD, BaseColor.BLACK)
        table.defaultCell.border = Rectangle.BOTTOM
        table.widthPercentage = 100f
        table.defaultCell.setPadding(10f)
        table.addCell(Phrase("Questions ".toUpperCase(), titleFont))
        document.add(table)
        addBreak(document)
        val list = qustionList
        val pointColumnWidths = floatArrayOf(8f, 2f)
        table = PdfPTable(pointColumnWidths)
        table.widthPercentage = 100f
        // table.defaultCell.setPadding(5f)
        titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL, BaseColor.BLACK)
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.WHITE)

        list.forEach {
            var cell: PdfPCell
            // /storage/emulated/0/21_images/488_images/488_image799089554.png
            if (it.type == Constant.QUSTION_TYPE_SECTIONTITTLE) {
                cell = PdfPCell(
                    Phrase(
                        it.title,
                        sectionFont
                    )
                )
                cell.backgroundColor = BaseColor(217, 217, 217)
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                cell.colspan = 2
                cell.setPadding(5f)

            } else {
                var paragraph = Paragraph()
                paragraph.add(
                    Phrase(
                        it.title,
                        titleFont
                    )
                )
                if (!it.comments.isNullOrEmpty()) {
                    paragraph.add(
                        Phrase("\nAdditional Comment : " + it.comments, titleFont)
                    )
                }
                cell = PdfPCell(
                )
                cell.setPadding(5f)
                cell.addElement(paragraph)
            }
            if (!it.imageUris.isNullOrEmpty()) {
                // cell.minimumHeight = 160f
                drawImages(it, cell, it.imageUris)

            }

            table.addCell(
                cell
            )
            if (it.type != Constant.QUSTION_TYPE_SECTIONTITTLE) {
                cell = PdfPCell(
                    Phrase(
                        if (it.getAnswer() == "1") "YES".toUpperCase() else if (it.getAnswer() == "0") "NO".toUpperCase() else "NA",
                        valueFont
                    )
                )
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                cell.setPadding(5f)

                if (it.fail == it.getAnswer()) {
                    cell.backgroundColor = BaseColor(198, 40, 40)
                } else if (it.fail != it.getAnswer() && it.getAnswer() != "-1") {
                    cell.backgroundColor = BaseColor(67, 160, 71)
                } else {
                    cell.backgroundColor = BaseColor(102, 102, 102)
                }
                table.addCell(
                    cell
                )
            } else {
                cell = PdfPCell(
                    Phrase(
                        "", valueFont
                    )
                )
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                cell.setPadding(5f)
                /* table.addCell(
                     cell
                 )*/
            }
        }
        document.add(table)
    }

    private fun drawImages(
        questionModel: BaseQustion,
        celll: PdfPCell,
        list: ArrayList<String>?
    )  {
        try {//    addBreak(celll)
            var file = File(
                Utils.getFolderPath(
                    context,
                    questionModel.getId(),
                    questionModel.getToolId()
                )
            )
            var listofImages=file.listFiles { file, s ->
                run {
                    var nameSplit = s.split("_")
                    Log.e("TAG", "Name $s")
                    Log.e("TAG", "Name ${file.name}")
                    Log.e("TAG", "Size ${nameSplit.size}")
                    nameSplit.size == 3
                }
            }
            var filesize =listofImages.size
            val x = celll.width / 4
            var paragraph = Paragraph()
            var table: PdfPTable
            table = when {
                filesize < 3 -> PdfPTable(filesize)
                filesize >= 3 -> {
                    PdfPTable(3)
                }
                else -> PdfPTable(filesize / 3)
            }

            table.defaultCell.setPadding(2f)
            table.defaultCell.border = Rectangle.NO_BORDER

            if (!list.isNullOrEmpty()) {
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)
                paragraph.add(Phrase(questionModel.getTittle(), titleFont))
                try {
                    Log.e("TAG", "No of Images ${listofImages.size}")
                   listofImages.forEach { file1 ->
                        var bitmap = BitmapFactory.decodeFile(file1.absolutePath)
                        bitmap = getResizedBitmap(bitmap, 200)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val image = Image.getInstance(stream.toByteArray())
                        image.scaleToFit(200f, 200f)
                        var chunk = Chunk(image, 100f, 100f)
                        // paragraph.add(chunk)
                        // celll.addElement(image)
                        //image.alignment=
                        var imageCell = PdfPCell(image, true)
                        imageCell.paddingTop = 5f
                        imageCell.paddingRight = 5f
                        imageCell.paddingBottom = 5f
                        imageCell.horizontalAlignment = Element.ALIGN_LEFT
                        imageCell.border = Rectangle.NO_BORDER
                        imageCell.fixedHeight = 160f
                        imageCell.addElement(image)
                        table.addCell(imageCell)
                    }
                    if (filesize > 3) {
                        while (filesize % 3 != 0) {
                            filesize += filesize / 3
                        }
                        for (i in 0 until filesize - listofImages.size) {
                            var imageCell = PdfPCell()
                            imageCell.paddingTop = 5f
                            imageCell.paddingRight = 5f
                            imageCell.paddingBottom = 5f
                            imageCell.horizontalAlignment = Element.ALIGN_LEFT
                            imageCell.border = Rectangle.NO_BORDER
                            imageCell.fixedHeight = 160f
                            imageCell.addElement(Phrase(""))
                            table.addCell(imageCell)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                /*  // paragraph.add(table)
                   if (questionModel.getQuestionTypeId() == Constant.QUSTION_TYPE_SIGNATURE) {
                       paragraph.add(Phrase("Date : " + questionModel.getAnswer(), titleFont))
                   }*/
                celll.addElement(table)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getResizedBitmap(image: Bitmap, width: Int, height: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = width
            height = (width / bitmapRatio).toInt()
        } else {
            height = height
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun drawFeedBack(document: Document) {
        //   document.newPage()

        var table = PdfPTable(1)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)

        table.defaultCell.border = Rectangle.BOTTOM
        table.widthPercentage = 100f
        //  table.defaultCell.setPadding(5f)
        table.addCell(Phrase("Feedback Summary ".toUpperCase(), titleFont))
        document.add(table)
        titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)

        var mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor(153, 153, 153))
        var mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        table = PdfPTable(1)
        table.widthPercentage = 100f
        table.defaultCell.border = Rectangle.NO_BORDER
        var mainTable = PdfPTable(1)
        mainTable.defaultCell.border = Rectangle.BOX
        mainTable.widthPercentage = 100f
        // mainTable.defaultCell.setPadding(0f)

        mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.add(mOrderDetailsTitleParagraph)
        //  table.defaultCell.setPadding(10f)
        var list = dao.getFeedBackNonlive(templateModel.id.toString())
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT
        var listOfsign = list.filter { it.getQuestionTypeId() == Constant.QUSTION_TYPE_SIGNATURE }
        var signModelList = ArrayList<SignatureModel>()
        listOfsign.forEach {
            var signatureModel = SignatureModel(it)
            var nameModel = list[list.indexOf(it) - 1]
            if (nameModel.title?.contains("Name", true)!!) {
                signatureModel.nameModel = nameModel
            }
            signModelList.add(signatureModel)
        }
        signModelList.forEach {
            if (it.nameModelIsNotNullOrEmpty())
                list = list.minus(it.nameModel)
            list = list.minus(it.feedBackModel)
        }
        list.forEach {
            mOrderDetailsTitleChunk = Chunk(
                it.title,
                valueFont
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleChunk = Chunk(
                "\n" + it.getAnswer(),
                titleFont
            )
            mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            table.addCell(mOrderDetailsTitleParagraph)

            if (!it.imageUris.isNullOrEmpty())
                drawImages(it, table.getRow(table.rows.size - 1).cells[0], it.imageUris)

        }
        drawSignatureImage(table, signModelList)
        mainTable.addCell(table)
        document.add(mainTable)
    }

    private fun drawSignatureImage(
        celll: PdfPTable,
        list: ArrayList<SignatureModel>?
    ) {

        list?.forEach {
            var file = File(
                Utils.getFolderPath(
                    context,
                    it.feedBackModel.getId(),
                    it.feedBackModel.getToolId()
                )
            )
            var paragraph = Paragraph()


            var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)

            var mOrderDetailsTitleChunk = Chunk(
                "   ",
                titleFont
            )
            var valueFont =
                Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor(153, 153, 153))
            var mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            mOrderDetailsTitleChunk = Chunk(
                "   ",
                titleFont
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            //  table.defaultCell.setPadding(10f)

            if (it.nameModelIsNotNullOrEmpty()) {
                mOrderDetailsTitleChunk = Chunk(
                    it.nameModel.title,
                    titleFont
                )
                mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
                mOrderDetailsTitleChunk = Chunk(
                    " " + it.nameModel.getAnswer() + "\n",
                    valueFont
                )
                mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
                mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
                //  table.widthPercentage = 100f
                celll.addCell(mOrderDetailsTitleParagraph)
            }
            if (!list.isNullOrEmpty()) {
                var table = PdfPTable(1)
                table.defaultCell.border = Rectangle.NO_BORDER
                var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)
                table.addCell(Phrase(it.feedBackModel.getTittle(), titleFont))
                try {
                    var listFile = file.listFiles()
                    listFile.sortWith(Comparator { t, t2 ->
                        t2.lastModified().compareTo(t.lastModified())
                    })
                    var file1 = listFile[0]
                    var bitmap = BitmapFactory.decodeFile(file1.absolutePath)
                    bitmap = getResizedBitmap(bitmap, 300, 200)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val image = Image.getInstance(stream.toByteArray())
                    image.scaleToFit(300f, 200f)
                    var chunk = Chunk(image, 100f, 100f)
                    //paragraph.add(chunk)
                    // celll.addElement(image)
                    //image.alignment=
                    var imageCell = PdfPCell(image, true)
                    imageCell.horizontalAlignment = Element.ALIGN_LEFT
                    imageCell.border = Rectangle.NO_BORDER
                    imageCell.fixedHeight = 120f
                    table.addCell(imageCell)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                //paragraph.add(imageTable)
                table.addCell(Phrase("Date : " + it.feedBackModel.getAnswer(), titleFont))
                celll.addCell(table)

            }
            //paragraph.add(Phrase("Date : " + it.feedBackModel.getAnswer(), titleFont))
            /* table.addCell(imageTable)
             mainTable.addCell(table)*/

        }
        /*  celll.addElement(mainTable)*/

    }
}