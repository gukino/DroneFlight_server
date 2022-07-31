## Steps to deployment and start the service

Simple deployment of pic-share-server using Java Spring Boot 

1. Using "mvn clean package" to package the pic-share-server and get the "pic-share-0.1-SNAPSHOT.jar" file in target directory.

   ```
   mvn clean package
   ```

2. Upload the jar file to the cloud server for running.

3. Install jdk11 and MariaDB database on the cloud server.

   ```
   sudo apt install openjdk-11-jre-headless
   sudo apt install mariadb-server
   ```

4.  Import the "pic-share.sql" file into the database.  

   ```
   source xxx/droneflight.sql
   ```

5. Using "java -jar" command to start the service.

   ```
   nohup java -jar Drone_flight-0.1-SNAPSHOT.jar &
   ```
