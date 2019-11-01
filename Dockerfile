#docker build -t zesty-router .
FROM ubuntu:18.04
MAINTAINER Stephen Maina <m41na@yahoo.com>
RUN apt-get -y update
RUN apt-get install wget -y
CMD echo "install jdk 11"
RUN apt-get install openjdk-11-jre -y
RUN echo "$(java -version)"
CMD echo "install maven 3.6.2"
RUN bash -c "wget https://www-us.apache.org/dist/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.tar.gz -P /tmp"
RUN bash -c "tar xf /tmp/apache-maven-*.tar.gz -C /opt"
RUN bash -c "ln -s /opt/apache-maven-3.6.2 /opt/maven"
CMD echo "install git"
RUN apt-get install git -y
RUN echo "$(git version)"
CMD echo "update environment variables"
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
CMD echo "java home is set to $JAVA_HOME"
ENV MAVEN_HOME=/opt/maven
CMD echo "maven home set to $MAVEN_HOME"
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin
CMD echo "$(mvn -version)"
CMD echo "getting latest zesty-router from github"
RUN /usr/bin/git clone https://github.com/m41na/zesty-router.git
RUN cd ./zesty-router \
    && /opt/maven/bin/mvn clean package -U -DskipTests
COPY target/zesty-router-0.1.1-shaded.jar ../
RUN cd ..
EXPOSE 8080
ENTRYPOINT java -jar ./zesty-router-0.1.1-shaded.jar com.practicaldime.zesty.app.AppServer