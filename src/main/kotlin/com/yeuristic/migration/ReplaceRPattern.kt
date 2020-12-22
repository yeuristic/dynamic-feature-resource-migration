package com.yeuristic.migration

import com.yeuristic.ResourceData
import java.lang.StringBuilder
import java.util.regex.Pattern


inline fun replaceRPattern(input: String, newPrefix: String, replaceableBlock: (ResourceData) -> Boolean): String? {
    val stringBuilder = StringBuilder()
    val spacingAndCommentRegex = "(\\s|//[^\n]*\n|/\\*.*\\*/)*"
    val rPattern =
        Pattern.compile("(?<![\\.a-zA-Z0-9_])R$spacingAndCommentRegex\\.$spacingAndCommentRegex([a-zA-Z0-9_]+)$spacingAndCommentRegex\\.$spacingAndCommentRegex([a-zA-Z0-9_]+)")
    val rMatcher = rPattern.matcher(input)
    var baseResourceExist = false
    var lastIndex = 0
    while (rMatcher.find()) {
        val start = rMatcher.start()
        val end = rMatcher.end()
        val folderName = rMatcher.group(3)
        val fileName = rMatcher.group(6)
        val resourceData = ResourceData(folderName, fileName)
        if (replaceableBlock(resourceData)) {
            baseResourceExist = true
            stringBuilder.run {
                append(input.substring(lastIndex, start))
                append(newPrefix)
                append(input.substring(start + 1, end + 1))
            }
            lastIndex = end + 1
        }
    }

    stringBuilder.append(input.substring(lastIndex))
    return stringBuilder.toString().takeIf { baseResourceExist }
}