package com.benAbdelWahed.utils


import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class AlertUtil(context: Context) {
    private var alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
    fun show() = alertDialog.show()
    fun positiveButton(text: String, onClick: DialogInterface.OnClickListener?) = alertDialog.setPositiveButton(text, onClick)
    fun positiveButton(text: Int, onClick: DialogInterface.OnClickListener?) = alertDialog.setPositiveButton(text, onClick)
    fun negativeButton(text: String, onClick: DialogInterface.OnClickListener?) = alertDialog.setNegativeButton(text, onClick)
    fun negativeButton(text: Int, onClick: DialogInterface.OnClickListener?) = alertDialog.setNegativeButton(text, onClick)
    fun message(text: String) = alertDialog.setMessage(text)
    fun message(text: Int) = alertDialog.setMessage(text)
    fun title(text: String) = alertDialog.setTitle(text)
    fun title(text: Int) = alertDialog.setTitle(text)
}