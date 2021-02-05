package com.yeuristic.migration.xml

import com.yeuristic.ResourceData
import com.yeuristic.migration.BaseRMigrationHandler
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import java.io.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import org.jdom2.filter.Filters.document




class XmlFileRMigrationHandler(val xmlWriter: XmlWriter) : BaseRMigrationHandler {
    override fun handleMigration(file: File, baseRFullPath: String, moduleResourceData: Set<ResourceData>) {
        val inputFile = file
        val outputFile = file
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile).apply {
            documentElement.normalize()
            xmlStandalone = true
        }
        val layoutElements = xmlDoc.getElementsByTagName("layout")
        if (layoutElements.length != 1) {
            return
        }
        val layoutChildElements = layoutElements.item(0).elementNodes()
        var found = false
        //if layout contains data and UI layout's root element
        if (layoutChildElements.size == 2 && layoutChildElements.first().nodeName == "data") {
            val dataNode = layoutChildElements[0]
            val rootNode = layoutChildElements[1]
            var useContextCompat = false
            rootNode.postOrderElementTraversal {
                val element = it as Element
                val attributeMap = element.attributes
                val idNode = attributeMap.getNamedItem("android:id")
                if (idNode != null) {
                    val id = idNode.nodeValue.replace("@+id/", "").replace("@id/", "")
                    for (i in 0 until attributeMap.length) {
                        val itemNode = attributeMap.item(i)
                        if (itemNode.nodeValue.startsWith("@{")) {
                            val value = itemNode.nodeValue
                            val matcher = Pattern.compile("@([a-zA-Z0-9_]+)/([a-zA-Z0-9_]+)").matcher(value)
                            val stringBuilder = StringBuilder()
                            var lastIndex = 0
                            while (matcher.find()) {
                                val start = matcher.start()
                                val end = matcher.end()
                                val folderName = matcher.group(1)
                                val fileName = matcher.group(2)

                                val resourceData = ResourceData(folderName, fileName)
                                if (moduleResourceData.contains(resourceData).not()) {
                                    val camelCasedId = toCamelCase(id)

                                    stringBuilder.run {
                                        append(value.substring(lastIndex, start))
                                        when (folderName) {
                                            "drawable", "color" -> {
                                                composeContextCompat(camelCasedId, baseRFullPath, resourceData)
                                                useContextCompat = true
                                            }
                                            else -> {
                                                composeDefaultResource(camelCasedId, baseRFullPath, resourceData)
                                            }
                                        }
                                    }
                                    lastIndex = end
                                }
                            }
                            if (stringBuilder.isNotEmpty()) {
                                stringBuilder.append(value.substring(lastIndex))
                                itemNode.textContent = stringBuilder.toString()
                                found = true
                            }
                        }
                    }
                }
            }
            if (found) {
                if (useContextCompat) {
                    val importElement: Element = xmlDoc.createElement("import").apply {
                        setAttribute("type", "androidx.core.content.ContextCompat")
                    }
                    dataNode.appendChild(xmlDoc.createTextNode("    "))
                    dataNode.appendChild(importElement)
                    dataNode.appendChild(xmlDoc.createTextNode("\n    "))
                }
                xmlWriter.write(layoutElements.item(0), BufferedWriter(FileWriter(outputFile)))
            }
        }
    }

    private fun StringBuilder.composeDefaultResource(
        camelCasedId: String,
        baseRFullPath: String,
        resourceData: ResourceData
    ) {
        append(camelCasedId)
        val getFolderName = if (resourceData.folderName == "dimen") {
            "dimension"
        } else {
            resourceData.folderName
        }.let {
            toCamelCase("get_$it")
        }
        append(".getResources().")
        append(getFolderName)
        append("(")
        append(baseRFullPath)
        append(".")
        append(resourceData.folderName)
        append(".")
        append(resourceData.fileName)
        append(")")
    }

    private fun StringBuilder.composeContextCompat(
        camelCasedId: String,
        baseRFullPath: String,
        resourceData: ResourceData
    ) {
        append("ContextCompat.")
        val getFolderName = toCamelCase("get_${resourceData.folderName}")
        append(getFolderName)
        append("(")
        append(camelCasedId)
        append(".getContext(), ")
        append(baseRFullPath)
        append(".")
        append(resourceData.folderName)
        append(".")
        append(resourceData.fileName)
        append(")")
    }

    private fun toCamelCase(snakeCase: String): String {
        return buildString {
            var previouslyUnderScore = false
            snakeCase.forEach {
                if (it == '_') {
                    previouslyUnderScore = true
                } else {
                    if (previouslyUnderScore) {
                        append(it.toUpperCase())
                    } else {
                        append(it)
                    }
                    previouslyUnderScore = false
                }
            }
        }
    }

    private fun Node.postOrderElementTraversal(f: (Node) -> Unit) {
        val childElements = elementNodes()
        if (childElements.isNotEmpty()) {
            childElements.forEach {
                it.postOrderElementTraversal(f)
            }
        }
        f(this)
    }

    private fun Node.elementNodes(): List<Node> {
        val copy = ArrayList<Node>(childNodes.length)
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                copy.add(node)
            }
        }
        return copy
    }
}