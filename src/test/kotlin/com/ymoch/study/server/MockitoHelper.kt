@file:Suppress("UNCHECKED_CAST")

package com.ymoch.study.server

import org.mockito.ArgumentMatchers

class MockitoHelper {
    companion object {
        fun <T> any(): T = ArgumentMatchers.any() ?: null as T
    }
}
