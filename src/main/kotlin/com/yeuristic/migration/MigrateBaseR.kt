package com.yeuristic.migration

import com.yeuristic.ResourceData
import com.yeuristic.migration.java.JavaFileRMigrationHandler
import com.yeuristic.migration.kotlin.KotlinFileRMigrationHandler
import com.yeuristic.migration.xml.AndroidStyleXmlWriter
import com.yeuristic.migration.xml.XmlFileRMigrationHandler
import java.io.File

object MigrateBaseR {
    val migrationHandlerMap = mapOf(
        "kt" to KotlinFileRMigrationHandler,
        "java" to JavaFileRMigrationHandler,
        "xml" to XmlFileRMigrationHandler(AndroidStyleXmlWriter("1.0", "utf-8", 4))
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