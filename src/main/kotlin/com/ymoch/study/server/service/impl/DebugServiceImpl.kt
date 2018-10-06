package com.ymoch.study.server.service.impl

import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.record.debug.ExceptionRecord
import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import org.springframework.web.context.WebApplicationContext
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.reflect.jvm.jvmName

@Service
@Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class DebugServiceImpl(
        @Value("\${debugging:false}") private val debugMode: Boolean
) : DebugService {
    // When request debug mode is on, then recorder is not null.
    // When request debug mode is off, then recorder is null.
    var recorder: Recorder? = null

    override fun debugModeEnabled() = debugMode

    override fun enableRequestDebugMode() {
        if (!debugMode) {
            return
        }
        recorder = Recorder()
    }

    override fun registerException(exception: Exception) {
        recorder?.registerException(exception)
    }

    override fun createRequestDebugRecord(): DebugRecord? {
       return recorder?.toDebugRecord()
    }
}

class Recorder {
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
            ExceptionRecord(className, stackTrace)
        }
        return DebugRecord(exception = exceptionRecord)
    }
}

fun toStackTraceLine(element: StackTraceElement): String {
    val source = when {
        element.isNativeMethod -> "Native Method"
        element.fileName == null -> "Unknown Source"
        element.lineNumber < 0 -> element.fileName
        else -> "${element.fileName}:${element.lineNumber}"
    }
    return "${element.className}.${element.methodName}($source)"
}
