package com.robohorse.gpversionchecker.domain

interface VersionInfoListener {
    fun onResulted(version: Version?)
    fun onErrorHandled(throwable: Throwable?)
}
