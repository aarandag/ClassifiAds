#!/usr/bin/make -f
# -*- mode:makefile -*-

JC 				= javac
JVM				= java
CLASSPATH		:= "$(CLASSPATH):lib/jsoup-1.10.3.jar"
CLASSDIR 		= ../classes
JFLAGS 			= -d $(CLASSDIR) -cp $(CLASSPATH)
JADE 			= jade.Boot -agents
PARAMS 			= 	"printer:presentation.PrinterAgent("processor")\
					;processor:domain.ProcessorAgent\
					;retriever1:domain.RetrieverAgent("www.as.com")\
					;retriever2:domain.RetrieverAgent("www.sports.es")\
					;retriever3:domain.RetrieverAgent("www.mundodeportivo.com")\
					;retriever4:domain.RetrieverAgent("www.marca.com")" 
SRC1 			= src/domain
SRC2			= src/presentation

compile:
	$(JC) $(JFLAGS) $(SRC1)/*.java $(SRC2)/*.java

run:
	$(JVM) -cp $(CLASSPATH) $(JADE) $(PARAMS)

clean:
	$(RM) $(CLASSES)/domain/*.class $(CLASSES)/presentation/*.class
