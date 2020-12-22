package com.yeuristic.migration

import com.yeuristic.ResourceData
import java.io.File
import java.lang.StringBuilder
import java.util.regex.Pattern

object KotlinFileRMigrationHandler : BaseRMigrationHandler {

    const val BASE_R = "BaseR"
    override fun handleMigration(
        file: File,
        baseRFullPath: String,
        moduleResourceData: Set<ResourceData>
    ) {
        val stringBuilder = StringBuilder()

        val reader = file.bufferedReader()
        val sourceCode = reader.readText()
        val packageIndex = sourceCode.indexOf("package")
        val endOfPackageIndex = sourceCode.indexOf('\n', packageIndex) + 1

        val asBaseRPattern = Pattern.compile("import\\s([a-zA-Z0-9_]|\\.)+\\sas\\s$BASE_R")
        val asBaseRMatcher = asBaseRPattern.matcher(sourceCode)
        if (!asBaseRMatcher.find()) {
            stringBuilder.run {
                append(sourceCode.substring(0, endOfPackageIndex))
                append(System.lineSeparator())
                append("import $baseRFullPath as BaseR")
                append(System.lineSeparator())
            }
        }

        val replacedSourceCode = replaceRPattern(sourceCode.substring(endOfPackageIndex), BASE_R) {
            !moduleResourceData.contains(it)
        }

        if (replacedSourceCode != null) {
            stringBuilder.append(replacedSourceCode)
            file.writeText(stringBuilder.toString())
        }
    }
}