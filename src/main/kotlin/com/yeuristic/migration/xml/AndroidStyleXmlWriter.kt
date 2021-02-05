package com.yeuristic.migration.xml

import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import java.io.ByteArrayOutputStream
import java.io.Writer
import java.util.*

class AndroidStyleXmlWriter(
    private val version: String,
    private val encoding: String,
    indentAmount: Int,
    private val newLine: String = "\n"
) : XmlWriter {

    private val indentation = " ".repeat(indentAmount)
    private val dom: DOMImplementationLS = DOMImplementationRegistry.newInstance().getDOMImplementation("LS") as DOMImplementationLS
    private val serializer: LSSerializer = dom.createLSSerializer().apply {
        newLine = this@AndroidStyleXmlWriter.newLine
        domConfig.setParameter("xml-declaration", false)
    }

    override fun write(node: Node, out: Writer) {
        out.write("<?xml version=\"$version\" encoding=\"$encoding\"?>")
        out.write(newLine)
        dfsNode(node, out)
        out.close()
    }

    private fun dfsNode(node: Node, writer: Writer, depth: Int = 0) {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val element = node as Element
            writer.run {
                write("<")
                write(element.nodeName)
            }
            writer.writeAttrs(element, depth)
            val childElements = node.childNodes
            if (childElements.length != 0) {
                writer.write(">")
                for (i in 0 until  childElements.length) {
                    val childNode = childElements.item(i)
                    dfsNode(childNode, writer, depth + 1)
                }

                writer.run {
                    write("</")
                    write(element.nodeName)
                    write(">")
                }

            } else {
                writer.write(" />")
            }
        } else {
            val destination = dom.createLSOutput()
            destination.encoding = "utf-8"
            val bos = ByteArrayOutputStream()
            destination.byteStream = bos
            serializer.write(node, destination)

            val string = String(bos.toByteArray())
            writer.write(string)
        }
    }

    private fun Writer.writeAttrs(element: Element, depth: Int) {
        val attrIndentation = indentation.repeat(depth + 1)
        val sortedAndroidAttrs = element.attributes.toSortedAndroidAttrs()
        for (attr in sortedAndroidAttrs) {
            if (sortedAndroidAttrs.size == 1) {
                write(" ")
            } else {
                write(newLine)
                write(attrIndentation)
            }
            write(attr.nodeName)
            write("=")
            write("\"")
            write(attr.nodeValue)
            write("\"")
        }
    }

    private fun NamedNodeMap.toSortedAndroidAttrs(): List<Node> {
        val result = LinkedList<Node>()
        var idNode: Node? = null
        var styleNode: Node? = null
        var layoutWidthNode: Node? = null
        var layoutHeightNode: Node? = null
        val xmlnsNodesStack = Stack<Node>()
        val androidLayoutNodesStack = Stack<Node>()
        for (i in 0 until length) {
            val attrNode = item(i)
            when(attrNode.nodeName) {
                "android:id" -> idNode = attrNode
                "style" -> styleNode = attrNode
                "android:layout_width" -> layoutWidthNode = attrNode
                "android:layout_height" -> layoutHeightNode = attrNode
                else -> {
                    if (attrNode.nodeName.startsWith("xmlns:")) {
                        xmlnsNodesStack.push(attrNode)
                    }
                    else if (attrNode.nodeName.startsWith("android:layout")) {
                        androidLayoutNodesStack.push(attrNode)
                    } else {
                        result.addLast(attrNode)
                    }
                }
            }
        }
        while (androidLayoutNodesStack.isNotEmpty()) {
            val androidLayoutNode = androidLayoutNodesStack.pop()
            result.addFirst(androidLayoutNode)
        }
        layoutHeightNode?.let { result.addFirst(it) }
        layoutWidthNode?.let { result.addFirst(it) }
        styleNode?.let { result.addFirst(it) }
        idNode?.let { result.addFirst(it) }
        while (xmlnsNodesStack.isNotEmpty()) {
            val xmlnsNode = xmlnsNodesStack.pop()
            result.addFirst(xmlnsNode)
        }
        return result
    }

}