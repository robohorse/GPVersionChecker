package com.robohorse.gpversionchecker.domain

import java.io.Serializable

data class Version(
        val newVersionCode: String? = null,
        val changes: String? = null,
        val isNeedToUpdate: Boolean = false,
        val url: String? = null,
        val description: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 2635L
    }
}
