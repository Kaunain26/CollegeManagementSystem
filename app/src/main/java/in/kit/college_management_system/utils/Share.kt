package `in`.kit.college_management_system.utils;


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


object Share {
    fun shareFile(context: Context, file: File) {
        val attachmentUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName,
            file
        )
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri)
        context.startActivity(Intent.createChooser(sharingIntent, "send bug report"))
    }
}