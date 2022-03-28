package `in`.kit.college_management_system.utils

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogHelperClass(val context: Context) {

    private val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
    var listener: OnAlertDialogActionPerformed? = null

    interface OnAlertDialogActionPerformed {
        fun positiveAction(dialog: DialogInterface)
        fun negativeAction(dialog: DialogInterface)
    }

    fun build(title: String, message: String, positiveBtnText: String, negativeBtnText: String) {

        try {
            listener = context as OnAlertDialogActionPerformed
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        materialAlertDialogBuilder.setTitle(title)
        materialAlertDialogBuilder.setMessage(message)

        materialAlertDialogBuilder.setPositiveButton(positiveBtnText) { dialog, _ ->
            listener?.positiveAction(dialog)
        }
        materialAlertDialogBuilder.setNegativeButton(negativeBtnText) { dialog, _ ->
            listener?.negativeAction(dialog)
        }
    }

    fun isCancelable(boolean: Boolean) {
        materialAlertDialogBuilder.setCancelable(boolean)
    }

    fun show() {
        materialAlertDialogBuilder.show()
    }


}