package com.yeuristic.migration

import com.yeuristic.ResourceData
import java.io.File
import java.lang.StringBuilder

object JavaFileRMigrationHandler : BaseRMigrationHandler  {
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