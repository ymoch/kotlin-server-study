package com.ymoch.study.server.service

import com.ymoch.study.server.record.ErrorRecord
import java.lang.Exception

interface ErrorService {
    fun createRecord(exception: Exception): ErrorRecord
}