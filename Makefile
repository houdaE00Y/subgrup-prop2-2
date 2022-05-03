CLASES = src/dominio/clases
CONTROLADORES = src/dominio/controladores

default: class

class:
	javac -d bin -cp $(CONTROLADORES)/junit-4.13.2.jar:$(CONTROLADORES)/hamcrest-core-1.2.jar $(CLASES)/*.java $(CONTROLADORES)/*.java
	cp -r src/persistencia bin/persistencia
	
run:
	java -cp $(CONTROLADORES):bin presentacion.Main
	
DCG:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverDCG
	
cjt:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverCjt_items
	
collaborative:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverCollaborative

content:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverContentBased

item:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverItem
	
kmeans:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverKMeans
	
slope:
	java -cp $(CONTROLADORES):bin dominio.controladores.drivers.DriverSlopeOne
	
test: 
	java -cp $(CONTROLADORES):$(CONTROLADORES)/junit-4.13.2.jar:$(CONTROLADORES)/hamcrest-core-1.2.jar:bin dominio.controladores.drivers.MyDistanceTestRunner
	
valoracio:
	java -cp $(CONTROLADORES):$(CONTROLADORES)/junit-4.13.2.jar:$(CONTROLADORES)/hamcrest-core-1.2.jar:bin dominio.controladores.drivers.DriverValoracio

clean:
	rm -rf bin/*
