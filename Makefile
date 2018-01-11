JC = javac
JVM= java

JFLAGS = -d ../classes -cp "lib/jade.jar:lib/jsoup-1.10.3.jar"
JADE = jade.Boot -agents

PARAMS = "processor:domain.ProcessorAgent("retriever1", "retriever2", "retriever3", "retriever4")\
		;retriever1:domain.RetrieverAgent("www.as.com");retriever2:domain.RetrieverAgent("www.sports.es")\
		;retriever3:domain.RetrieverAgent("www.mundodeportivo.com")\
		;retriever4:domain.RetrieverAgent("www.marca.com")" 

SOURCE = src/domain
CLASSES = ../classes

compile:
	$(JC) $(JFLAGS) $(SOURCE)/*.java

run:
	$(JVM) -cp "lib/jade.jar:lib/jsoup-1.10.3.jar" $(JADE) $(PARAMS)

clean:
	$(RM) $(CLASSES)/domain/*.class