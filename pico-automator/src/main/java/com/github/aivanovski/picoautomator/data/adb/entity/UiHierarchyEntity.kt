package com.github.aivanovski.picoautomator.data.adb.entity

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "hierarchy")
internal class UiHierarchyEntity {
    @set:XmlAttribute(name = "rotation")
    var rotation: Int? = null

    @set:XmlElement(name = "node")
    var nodes: List<UiNodeEntity> = mutableListOf()
}