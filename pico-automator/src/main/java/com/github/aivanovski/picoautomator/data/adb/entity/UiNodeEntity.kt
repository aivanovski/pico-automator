package com.github.aivanovski.picoautomator.data.adb.entity

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "node")
class UiNodeEntity {
    @set:XmlAttribute(name = "resource-id", required = false)
    var resourceId: String? = null

    @set:XmlAttribute(name = "package", required = false)
    var packageName: String? = null

    @set:XmlAttribute(name = "class", required = false)
    var className: String? = null

    @set:XmlAttribute(name = "bounds", required = false)
    var bounds: String? = null

    @set:XmlAttribute(name = "text", required = false)
    var text: String? = null

    @set:XmlElement(name = "node", required = false)
    var nodes: List<UiNodeEntity> = mutableListOf()
}