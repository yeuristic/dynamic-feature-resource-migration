package com.yeuristic.migration.java

import com.yeuristic.ResourceData
import com.yeuristic.migration.BaseRMigrationHandler
import com.yeuristic.migration.replaceRPattern
import java.io.File
import java.lang.StringBuilder

object JavaFileRMigrationHandler : BaseRMigrationHandler {
    override fun handleMigration(
        file: File,
        baseRFullPath: String,
        moduleResourceData: Set<ResourceData>
    ) {
        val stringBuilder = StringBuilder()

        val reader = file.bufferedReader()
        val sourceCode = reader.readText()

        val replacedSourceCode = replaceRPattern(sourceCode, baseRFullPath) {
            !moduleResourceData.contains(it)
        }

        if (replacedSourceCode != null) {
            stringBuilder.append(replacedSourceCode)
            file.writeText(stringBuilder.toString())
        }
    }
}