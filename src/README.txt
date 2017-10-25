1. Make sure you have java jdk/jvm 1.8 installed ("1.8.0_121")
2. Using terminal, go to source folder (extracted from the Lab2_source_code.zip)
3. Compile:
	javac *.java
	jar cvfe DnsClient.jar DnsClient *.class

4. To run:
	java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server domainname