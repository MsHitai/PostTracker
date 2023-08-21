FROM tomcat:10.1.12
COPY target/*.war /usr/local/tomcat/webapps/