JC 			= javac
JVM			= java
CLASSPATH	:= "$(CLASSPATH):lib/jade.jar:lib/jsoup-1.10.3.jar"
CLASSDIR 	= ../classes
JFLAGS 		= -d $(CLASSDIR) -cp $(CLASSPATH)
JADE 		= jade.Boot -agents
PARAMS 		= 	"printer:presentation.PrinterAgent("processor");\
			processor:domain.ProcessorAgent("retriever1", "retriever2", "retriever3", "retriever4")\
			;retriever1:domain.RetrieverAgent("www.as.com");retriever2:domain.RetrieverAgent("www.sports.es")\
			;retriever3:domain.RetrieverAgent("www.mundodeportivo.com")\
			;retriever4:domain.RetrieverAgent("www.marca.com")" 
SOURCE 		= src/domain

compile:
	$(JC) $(JFLAGS) $(SOURCE)/*.java

run:
	$(JVM) -cp $(CLASSPATH) $(JADE) $(PARAMS)

clean:
	$(RM) $(CLASSES)/domain/*.class
