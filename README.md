# image-recognition
invoke a python-trained tensorflow model from java programs,Real-time image recognition,and deploy the applications to springcloud dataflow local server

this is my Graduation Project.

1. use spring cloud task,run the retrain.bat

retrain.bat runs the tensorflow-master/tensorflow/examples/image_retraining/retrain.py ,and uses the inception-v3 model #inception-2015-12-05.tgz

After execution,under the classpath,two files are generated,output_graph.pb and output_labels.txt. 

Of course you can modfiy the application.properties to change the path

2. start source,processor,sink applications

you can modfiy their application.properties according to your programs

spring cloud stream source application: put picture in the configuration folder
spring cloud stream processor application: invoke the python-trained tensorflow model,and get the image recognition result
spring cloud stream sink application: just a simple ui used freemarker to show the result

3. now we can package the sateams,source,processor,sink
$ mvn clean package

4. start the data flow server

5. we can use the Data Flow Shell to deploy the sateams and task.
The Data Flow Shell is a client for the Data Flow Server. The shell allows us to perform the DSL command needed to interact with the server

$ java - jar spring-cloud-dataflow-shell-1.2.3.RELEASE.jar

dataflow>app register --name source --type source --uri maven://com.tse.graduation:springcloud-stream-rabbit-source:jar:0.0.1-SNAPSHOT

dataflow>app register --name processor --type processor --uri maven://com.tse.graduation:springcloud-stream-rabbit-processor:jar:0.0.1-SNAPSHOT

dataflow>app register --name sink --type sink --uri maven://com.tse.graduation:springcloud-stream-rabbit-sink:jar:0.0.1-SNAPSHOT

dataflow>stream create --definition "source --server.port=8093|processor --server.port=8094|sink --server.port=8095" --name mySimpleImageRecognition

dataflow>stream deploy --name ss --properties "app.source.spring.cloud.stream.bindings.output.destination=springcloudstream,
app.processor.spring.cloud.stream.bindings.input.destination=springcloudstream,
app.processor.spring.cloud.stream.bindings.output.destination=springcloudstreamsink,
app.sink.spring.cloud.stream.bindings.input.destination=springcloudstreamsink,
app.*.spring.cloud.stream.bindings.output.binder=rabbit1,app.*.spring.cloud.stream.bindings.input.binder=rabbit1"

Note:deploy the sateams,app.*.spring.cloud.stream.bindings.output.destination should be consistent with your programs.

6. the you can see the logs and visit the sink application's ip:port/index to check the result

