package com.yeuristic.migration

import com.yeuristic.ResourceData
import java.io.File

interface BaseRMigrationHandler {
    fun handleMigration(file: File, baseRFullPath: String, moduleResourceData: Set<ResourceData>)
}