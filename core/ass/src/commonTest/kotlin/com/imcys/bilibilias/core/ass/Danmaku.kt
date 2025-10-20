package com.imcys.bilibilias.core.ass

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("i")
data class Root(
    @XmlChildrenName("chatserver")
    val chatserver: String,
    @XmlChildrenName("chatid") val chatid: Long,
    @XmlChildrenName("mission") val mission: String,
    @XmlChildrenName("maxlimit") val maxlimit: String,
    @XmlChildrenName("state") val state: String,
    @XmlChildrenName("real_name") val real_name: String,
    @XmlChildrenName("source") val source: String,
    @XmlSerialName("d")
    val danmakus: List<Danmaku>
)

@Serializable
@XmlSerialName("d")
data class Danmaku(
    val p: String,
    @XmlValue
    val content: String
)