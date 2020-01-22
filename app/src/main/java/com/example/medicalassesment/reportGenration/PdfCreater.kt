package com.example.medicalassesment.reportGenration

import android.content.Context
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.R
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.BaseQustion
import com.example.medicalassesment.models.TemplateModel
import com.example.medicalassesment.Utials.Constant
import com.example.medicalassesment.Utials.Utils
import com.itextpdf.text.*
import java.io.ByteArrayOutputStream
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Chunk
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import java.text.DecimalFormat
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
            )//context.resources.getDrawable(R.drawable.pcn)
            val bitDw = d as BitmapDrawable
            val bmp = bitDw.bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.scaleToFit(100f, 100f)
            val x = (PageSize.A4.width - image.scaledWidth) / 2
            val y = (PageSize.A4.height - (image.scaledHeight + 50))
            image.alignment = Element.ALIGN_CENTER
            //image.alignment=
            document.add(image)
            val mOrderDetailsTitleChunk = Chunk(
                templateModel.title,
                font
            )
            val mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)
            addScore(document)
            // drawFailItems(document)
            drawPrelimanryInfo(document)
            drawQustions(document)
            drawFeedBack(document)
            document.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.WHITE)

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
        var table = PdfPTable(2)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK)
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

        table.addCell(
            Phrase(
                "Score \n ${DecimalFormat("##.00").format((correct / qustionList.size) * 100)} ",
                titleFont
            )
        )
        table.addCell(
            Phrase(
                "fail \n $fail",
                titleFont
            )
        )
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
        table = PdfPTable(1)
        table.defaultCell.setPadding(5f)
        table.widthPercentage = 100f
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor(153, 153, 153))

        mOrderDetailsTitleChunk = Chunk(
            "Inspection Conducted On",
            valueFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleChunk = Chunk(
            "\n\n" + templateModel.inspectionConductedOn,
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
            "\n\n" + templateModel.inspectionConductedBy,
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
            "\n\n" + templateModel.inspectionConductedAt,
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
        table.defaultCell.setPadding(5f)
        table.addCell(Phrase("Preliminary Information ".toUpperCase(), titleFont))
        document.add(table)
        table = PdfPTable(1)
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
        table.defaultCell.setPadding(10f)
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
                        "\n\n" + (it.getDropDownItems()?.get(Integer.parseInt(it.getAnswer()))
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
                                answerLable =
                                    prefHelper.getState().state[stateindex].lgas[lgaindex].wards[Integer.parseInt(
                                        it.getAnswer()
                                    )].name
                            }
                            "State : " -> {
                                stateindex = Integer.parseInt(it.getAnswer())
                                answerLable =
                                    prefHelper.getState()
                                        .getName(Integer.parseInt(it.getAnswer()))

                            }
                            "LGA : " -> {
                                lgaindex = Integer.parseInt(it.getAnswer())
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
                        "\n\n" + answerLable,
                        titleFont
                    )
                }
            } else
                Chunk(
                    "\n\n" + it.getAnswer(),
                    titleFont
                )
            mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            table.widthPercentage = 100f
            // if (!it.imageUris.isNullOrEmpty())
            //  drawImages(document, cell, it.imageUris)
            table.addCell(mOrderDetailsTitleParagraph)
            if (!it.imageUris.isNullOrEmpty())
                drawImages(it, document, table.getRow(table.rows.size - 1).cells[0], it.imageUris)
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
        document.newPage()

        var table = PdfPTable(1)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)
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
        table.defaultCell.setPadding(10f)
        titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL, BaseColor.BLACK)
        var valueFont =
            Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.WHITE)

        list.forEach {
            var cell = PdfPCell(/*
                Phrase(
                    it.title,
                    titleFont
                )*/
            )
            // /storage/emulated/0/21_images/488_images/488_image799089554.png

            if (!it.imageUris.isNullOrEmpty()) {
                cell.minimumHeight = 250f
                drawImages(it, document, cell, it.imageUris)

            } else {
                cell = PdfPCell(
                    Phrase(
                        it.title,
                        titleFont
                    )
                )
                cell.setPadding(10f)
            }
            table.addCell(
                cell
            )
            cell = PdfPCell(
                Phrase(
                    if (it.getAnswer() == "1") "YES".toUpperCase() else "NO".toUpperCase(),
                    valueFont
                )
            )
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_CENTER
            cell.setPadding(10f)
            if (it.getAnswer() == "1")
                cell.backgroundColor = BaseColor(67, 160, 71)
            else cell.backgroundColor = BaseColor(198, 40, 40)
            table.addCell(
                cell
            )

        }
        document.add(table)
    }

    private fun drawImages(
        questionModel: BaseQustion,
        document: Document,
        celll: PdfPCell,
        list: ArrayList<String>?
    ) {
        //    addBreak(celll)
        var file = File(
            Utils.getFolderPath(
                context,
                questionModel.getId(),
                questionModel.getToolId()
            )
        )
        val x = celll.width / 4
        var paragraph = Paragraph()
        var table = PdfPTable(file.listFiles().size)
        table.defaultCell.fixedHeight = 230f
        table.defaultCell.setPadding(10f)
        table.defaultCell.border = Rectangle.NO_BORDER
        if (!list.isNullOrEmpty()) {
            var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)
            paragraph.add(Phrase(questionModel.getTittle(), titleFont))

            try {
                file.listFiles().forEach { file1 ->

                    Log.e("TAG", "Path " + file1.absolutePath)
                    var bitmap = BitmapFactory.decodeFile(file1.absolutePath)
                    bitmap = getResizedBitmap(bitmap, 400)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val image = Image.getInstance(stream.toByteArray())
                    image.scaleToFit(200f, 200f)
                    var chunk = Chunk(image, 0f, 0f)
                    // paragraph.add(chunk)
                    // celll.addElement(image)
                    //image.alignment=
                    table.addCell(image)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            paragraph.add(table)
            if (questionModel.getQuestionTypeId() == Constant.QUSTION_TYPE_SIGNATURE) {
                paragraph.add(Phrase("Date : " + questionModel.getAnswer(), titleFont))
            }
            celll.addElement(paragraph)
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

    private fun drawFeedBack(document: Document) {
        document.newPage()
        var table = PdfPTable(1)
        var titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)

        table.defaultCell.border = Rectangle.BOTTOM
        table.widthPercentage = 100f
        table.defaultCell.setPadding(5f)
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
        mOrderDetailsTitleChunk = Chunk(
            "   ",
            titleFont
        )
        mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
        mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
        document.add(mOrderDetailsTitleParagraph)
        table.defaultCell.setPadding(10f)
        val list = dao.getFeedBackNonlive(templateModel.id.toString())
        table.defaultCell.horizontalAlignment = Element.ALIGN_LEFT
        list.forEach {
            mOrderDetailsTitleChunk = Chunk(
                it.title,
                valueFont
            )
            mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleChunk = Chunk(
                "\n\n" + it.getAnswer(),
                titleFont
            )
            mOrderDetailsTitleParagraph.add(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            table.widthPercentage = 100f
            table.addCell(mOrderDetailsTitleParagraph)
            if (!it.imageUris.isNullOrEmpty())
                drawImages(it, document, table.getRow(table.rows.size - 1).cells[0], it.imageUris)

        }
        document.add(table)
    }
}