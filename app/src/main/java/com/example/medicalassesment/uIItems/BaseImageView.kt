package com.example.medicalassesment.uIItems

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.medicalassesment.GlideApp
import com.example.medicalassesment.R
import java.io.File
import java.util.jar.Attributes

class BaseImageView : LinearLayout {
    private lateinit var imageView: AppCompatImageView
    private var imageUri = ""
    lateinit var onDelete: () -> Unit

    constructor(context: Context) : super(context) {
        IniateUi(context)
    }

    constructor(context: Context, attrs: Attributes) : this(context)

    private fun IniateUi(context: Context) {
        var imageLayout = LayoutInflater.from(context).inflate(R.layout.base_image_view, this, true)
        imageView = findViewById(R.id.imageView)
        var deleteImage = imageLayout.findViewById<AppCompatImageView>(R.id.deleteImage)
        deleteImage.setOnClickListener {

            var alertDialog = AlertDialog.Builder(context);
            alertDialog.setTitle("Confirm Delete")
            alertDialog.setMessage("Are you sure you want to delete this image?")
            alertDialog.setNegativeButton(
                "Yes"
            ) { p0, _ ->
                run {
                    val file = File(imageUri)
                    file.delete()
                    onDelete()
                    p0.dismiss()
                }
            }
            alertDialog.setPositiveButton(
                "NO"
            ) { p0, _ -> p0.dismiss() }
            alertDialog.show()


        }
    }

    fun loadImage(imageUri: String) {
        this.imageUri = imageUri
        GlideApp.with(imageView)
            .applyDefaultRequestOptions(RequestOptions().override(200, 200))
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(ObjectKey(imageUri))
            .into(imageView)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
    }
    fun loadImage(imageUri: File) {

        GlideApp.with(imageView)
            .applyDefaultRequestOptions(RequestOptions().override(200, 200))
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(ObjectKey(imageUri))
            .into(imageView)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        this.imageUri = imageUri.absolutePath
    }
}