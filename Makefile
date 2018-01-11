JC 		= javac
JVM		= java
LIBS		= "$(CLASSPATH):lib/jade.jar:lib/jsoup-1.10.3.jar"
CLASSDIR 	= ../classes
JFLAGS 		= -d $(CLASSDIR) -cp $(LIBS)
JADE 		= jade.Boot -agents
PARAMS 		= 	"processor:domain.ProcessorAgent("retriever1", "retriever2", "retriever3", "retriever4")\
			;retriever1:domain.RetrieverAgent("www.as.com");retriever2:domain.RetrieverAgent("www.sports.es")\
			;retriever3:domain.RetrieverAgent("www.mundodeportivo.com")\
			;retriever4:domain.RetrieverAgent("www.marca.com")" 
SOURCE 		= src/domain

compile:
	$(JC) $(JFLAGS) $(SOURCE)/*.java

run:
	$(JVM) -cp $(LIBS) $(JADE) $(PARAMS)

clean:
	$(RM) $(CLASSES)/domain/*.class
