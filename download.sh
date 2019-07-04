echo "
this script will
1. check if you have JAVA_HOME set
2. check your version of java (recommended min sdk 1.9)
3. if check fails, exit script
4. check if M2_HOME or MAVEN_HOME is set
5. check your versionof maven (recommended min 3.6.1)
6. if check fails, exit script
7. check if the currenct directory is writable (for download)
8. if check fails, exit script
9. download latest version of zesty-router from github into the currenct folder
10. create a variable for the name of the the downloaded folder - ZESTY_HOME
11. change into ZESTY_HOME and execute 'mvn clean install -U'
12. when build is done, copy shaded jar from ZESTY_HOME/target into the current folder
13. delete the sources downloaded from github
14. notify user of succesful operation
15. exit ecript
"
echo
set -e

ZESTY_HOME="zesty-router"
if [ -d $ZESTY_HOME ] ; then
	rm -rf $ZESTY_HOME;
fi

if [ $? -ne 0 ] ; then
	echo remove the existing $ZESTY_HOME before proceeding
	exit
fi

javaHome=$JAVA_HOME
if [ -z $javaHome ] ; then
	echo no java home was found
	exit 1
fi

echo java version found $javaHome
echo

mavenHome=$M2_HOME
if [ -z $mavenHome ] ; then
	echo M2_HOME not found. Try MAVEN_HOME instead
	echo
fi
	
mavenHome=$MAVEN_HOME
if [[ -z $mavenHome ]] ; then
	echo maven home not found
	exit 1
fi

echo maven version found $mavenHome
echo

if [ -w `pwd` ]; then 
	echo "$(pwd) is WRITABLE"; 
	echo
else 
	echo "NOT WRITABLE"; 
	echo
fi

git clone https://github.com/m41na/zesty-router.git

if [ $? -ne 0 ] ; then
	echo the download did not complete successsfully
	exit 1
fi

cd $ZESTY_HOME

if [ -e pom.xml ] ; then
	echo pom.xml file exists. the build can commence
	echo
else
	echo pom.xml file does not exist
	exit 1
fi

echo start building artifacts
echo 

mvn clean package -U

if [ $? -ne 0 ] ; then
	echo the build did not complete successsfully
	exit 1
fi

cp target/zesty*shaded.jar ..

if [ $? -ne 0 ] ; then
	echo the built library was not copied successsfully
	exit 1
fi

cd ..

rm -rf $ZESTY_HOME

echo all done!