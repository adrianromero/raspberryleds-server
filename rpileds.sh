#!/bin/sh

CLASSPATH=rpileds.jar
CLASSPATH=$CLASSPATH:web-common-0.1.0.jar

CLASSPATH=$CLASSPATH:jetty-continuation-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-http-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-io-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-security-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-server-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-servlet-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:jetty-util-9.0.2.v20130417.jar
CLASSPATH=$CLASSPATH:servlet-api-3.0.jar

CLASSPATH=$CLASSPATH:jettison-1.3.3.jar

CLASSPATH=$CLASSPATH:/opt/pi4j/lib/pi4j-core.jar:/opt/pi4j/lib/pi4j-device.jar:/opt/pi4j/lib/pi4j-gpio-extension.jar

java -cp $CLASSPATH com.adr.rpi.leds.App
