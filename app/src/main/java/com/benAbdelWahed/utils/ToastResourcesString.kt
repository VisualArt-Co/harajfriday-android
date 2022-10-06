package com.benAbdelWahed.utils

import android.content.Context
import java.lang.Exception

sealed class ResourceString {
    abstract fun format(context: Context): String
}
class IdResourceString(val id: Int): ResourceString() {
    override fun format(context: Context): String {
        return context.getString(id)
    }
}
class TextResourceString(val text: String): ResourceString() {
    override fun format(context: Context): String {
        return text
    }
}
class KeyResourceString(val key: String, val alt: String): ResourceString() {
    override fun format(context: Context): String {
        return try{
            context.getString(context.resources.getIdentifier(key, "string", context.packageName))
        }
        catch (e:Exception)
        {
            alt
        }
    }
}
class FormatResourceString(val id: Int, val values: Array<Any>): ResourceString() {
    override fun format(context: Context): String {
        return context.getString(id, *values)
    }
}
class FormatResourceResource(val id: Int, val value: Int): ResourceString() {
    override fun format(context: Context): String {
        return context.getString(id, context.getString(value))
    }
}