package com.yeuristic.migration.xml

import org.w3c.dom.Node
import java.io.Writer

interface XmlWriter {
    fun write(node: Node, out: Writer)
}