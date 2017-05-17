mv thrift-jars src
cd src
javac -cp ./thrift-jars/\*\:gen-java: */*/*.java