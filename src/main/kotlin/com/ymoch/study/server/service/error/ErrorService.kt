package com.ymoch.study.server.service.error

import com.ymoch.study.server.record.error.ErrorRecord
import java.lang.Exception

interface ErrorService {
    fun createRecord(exception: Exception): ErrorRecord
}