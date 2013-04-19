Mole
====

Page Performance collector using WebDriver API ,BrowserMobProxy and HarStorage

Installation--Setup your local testing ENV:  
1. Ant and JDK 1.7 needed, setting the ANT_HOME, JAVA_HOME and Path correctly  
2. Install and start MongoDB  
3. Install Python 2.7.2+(Windows x86 MSI Installer (2.7.4)), add Python directories to the system path:C:\Python27\;C:\Python27\Scripts;  
4. Install PyGTK bundle (All-in-one): pygtk-all-in-one-2.24.2.win32-py2.7.msi  
5. Install pyrsvg: pyrsvg-2.32.1.win32-py2.7.msi  
6. Install setuptools : setuptools-0.6c11.win32-py2.7.exe  
7. run following commands(setting proper proxy if needed): easy_install pylons==1.0;  
easy_install webob==0.9.8;  
easy_install pymongo;  
8. download harstorage-1.0-py2.7.egg and run easy_install harstorage-1.0-py2.7.egg  
9. Create a skeleton config file for your application instance called production.ini: paster make-config "harstorage" production.ini  
10. specifying the config file you want to set up : paster setup-app production.ini  
11. run HAR Storage using the Paste HTTP server: paster serve production.ini  
12. check the  http://127.0.0.1:5000 to see if harstorage page is showing up  
12. download jenkins.war,and start it as local console mode: java -jar jenkins.war  
13. create the testing job to do the periodic running against the env under test  

*Notice:  
If you want to test with Chrome Browser, please download your ChromeDriver from: http://code.google.com/p/chromedriver/downloads/list, unzip it and configure your chromedriver.exe path in globalpara.xml under conf file  
If you want to test with IE Browser, please download you IEDriver from: http://code.google.com/p/chromedriver/downloads/list, unzip it and configure your IEDriverServer.exe path in globalpara.xml under conf file;
also, you need to enable all zones to protected mode from Internet Options security settings  

How to Use:  
1. Download and unzip the Mole project code to your local machine(configure the global parameter file needed to align with your local settings*)  
2. Execute mongodb.bat and harstorage_cmd.bat to start the mongo and harstorage services  
3. Run "ant deploy" in cmd window, the "\target" folder will be generated  
4. Execute "Runner.bat" under "\target" directory to launch the tests  
5. Check out the harstorage web page from：http://localhost:5000/  
*Notice: Serveral Parameters you may need to change on demand:  
1> In harstorage_cmd.bat file, you need to configure the harstorage installation path  
2> In mongodb.bat file, you need to configure the mongodb installation directory and --dbpath  
3> In /conf/globalPara.xml file, you need to set chromedriverpath and harstoragehost on your local machine  
4> Set /test/testng.xml file based on your test needs  
5> If you want to run the test continously, you can download jenkins.war, and run "java -jar jenkins.war" to make a periodic job  

Git Tips:  
1. cd directory  
2. git status (to check your recent changes on your local repo)  
3. git add . (add all the changes you made)  
4. git status  
5. git commit -m "any comments"  
6. git push  
7. git reset HEAD <file> (convert your local changes)  
8. git checkout .  
9. git pull  
