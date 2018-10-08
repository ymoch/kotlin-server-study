package com.ymoch.study.server.service.debug

import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.record.debug.ExceptionRecord
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.reflect.jvm.jvmName

class DebugRecorder {

    companion object {
        fun toStackTraceLine(element: StackTraceElement): String {
            val source = when {
                element.isNativeMethod -> "Native Method"
                element.fileName == null -> "Unknown Source"
                element.lineNumber < 0 -> element.fileName
                else -> "${element.fileName}:${element.lineNumber}"
            }
            return "${element.className}.${element.methodName}($source)"
        }
    }

    private var exception: Exception? = null

    fun registerException(exception: Exception) {
        this.exception = exception
    }

    fun toDebugRecord(): DebugRecord {
        val exceptionRecord = exception?.let { it ->
            val className = it::class.jvmName
            val stackTrace = Arrays.stream(it.stackTrace)
                    .map { toStackTraceLine(it) }
                    .collect(Collectors.toList())
            ExceptionRecord(className, it.message, stackTrace)
        }
        return DebugRecord(exception = exceptionRecord)
    }
}
