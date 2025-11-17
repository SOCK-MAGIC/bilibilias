package com.bilibilias.core.logger

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration
import java.io.File

public object JvmLoggingConfigurator {

    public fun configureLogging(
        logsFolder: File,
    ) {
        val builder: ConfigurationBuilder<BuiltConfiguration> =
            ConfigurationBuilderFactory.newConfigurationBuilder()

        // Console appender
        val charset = "UTF-8"
        val consoleLayoutBuilder: LayoutComponentBuilder = builder.newLayout("PatternLayout")
            .addAttribute(
                "pattern",
                "%d{yyyy-MM-dd HH:mm:ss} %-5level %c{10} %m%n%throwable",
            )
            .addAttribute("charset", charset)
        val consoleAppenderBuilder: AppenderComponentBuilder =
            builder.newAppender("STDOUT", "Console")
                .add(consoleLayoutBuilder)
        builder.add(consoleAppenderBuilder)

        // File appender
        val fileLayoutBuilder: LayoutComponentBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d [%-5level] %c: %msg%n%throwable")
            .addAttribute("charset", charset)
        val fileAppenderBuilder: AppenderComponentBuilder =
            builder.newAppender("FILE", "RollingFile")
                .addAttribute("fileName", "${logsFolder.absolutePath}/app.log")
                .addAttribute("filePattern", "${logsFolder.absolutePath}/app-%d{yyyy-MM-dd}.log")
                .add(fileLayoutBuilder)

        fileAppenderBuilder.addComponent(
            builder.newComponent("Policies")
                .addComponent(
                    builder.newComponent("TimeBasedTriggeringPolicy")
                        .addAttribute("interval", "1")
                        .addAttribute("modulate", true),
                ),
        )
        fileAppenderBuilder.addComponent(
            builder.newComponent("DefaultRolloverStrategy")
                .addAttribute("max", "3"),
        )
        builder.add(fileAppenderBuilder)

        // Root logger
        builder.add(
            builder.newRootLogger(Level.ALL)
                .add(builder.newAppenderRef("STDOUT").addAttribute("level", Level.DEBUG))
                .add(builder.newAppenderRef("FILE")),
        )
        builder.add(
            builder.newLogger("io.ktor.client.plugins", Level.DEBUG)
                .addAttribute("additivity", false),
        )

        Configurator.initialize(builder.build())
    }
}