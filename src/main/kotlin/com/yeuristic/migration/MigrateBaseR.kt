package com.yeuristic.migration

import com.yeuristic.ResourceData
import java.io.File

object MigrateBaseR {
    val migrationHandlerMap = mapOf(
        "kt" to KotlinFileRMigrationHandler,
        "java" to JavaFileRMigrationHandler
    )

    @JvmStatic
    fun migrateBaseR(srcPath: String, baseRFullPath: String, moduleResourceData: Set<ResourceData>) {
        File(srcPath).walk().forEach {
            if (it.isFile) {
                val handler = migrationHandlerMap[it.extension.toLowerCase()]
                handler?.run {
                    handleMigration(it, baseRFullPath, moduleResourceData)
                }
            }
        }
    }
}