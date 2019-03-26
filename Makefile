test:
	javac -cp .:jSerialComm.jar Simon.java Renderer.java SerialPortTest.java Audio.java
	java -cp .:jSerialComm.jar Simon
		
exec:
	java -cp .:jSerialComm.jar Simon

clean:
	rm *.class