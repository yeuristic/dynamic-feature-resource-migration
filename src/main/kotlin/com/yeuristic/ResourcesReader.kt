package com.yeuristic

import java.io.File

fun readResources(path: String): Set<ResourceData> {
    val resourceSet: MutableSet<ResourceData> = mutableSetOf()
    File(path).run {
        val bufferedReader = bufferedReader()
        var line = bufferedReader.readLine()
        while (line != null) {
            val temp = line.split(" ")
            if (temp.size > 2) {
                resourceSet.add(ResourceData(temp[1], temp[2]))
            }
            line = bufferedReader.readLine()
        }
    }
    return resourceSet
}