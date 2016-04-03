# OpenViSu Version 0.0.1
Open Video Surveillance, it started with an *Easter hacking session*...

##2016-04-03 Features already done
1. ZoneMinder rest api works (getting events, alarms, frames, monitors, pictures etc.) including SSL support.
2. Local caching of images for saving network bandwidth (configurable).
3. Prototyp of MP4 (H.264) video creation from jpgs works. Creted videos will be cached for replays.

##2016-03-31 What's next?
1. Do the AngularJS stuff (web pages) with Spring security.
2. Showing alarm frames, live monitors and walk around the archive images.
3. see thoughts below.

Thoughts by Kai Reinhard after an Easter hacking session. How to proceed?

###Do the web stuff
Should work responsive also on mobile devices (using Bootstrap with AngularJS)

###Play video
Videogular (including livestream)

###Support of Netavis
Waiting for the license key :-(

###ZoneMinder, Native access
Rest-API works better than expected. Remote servers should work through caching algorithms...
Native access to image directory and MySQL database instead of Rest-API not really needed?

###Integration of OpenCV
That would rock!

###Future?
Testing the performance of ZoneMinder (php), may-be implement another recording algorithm for saving cpu.



## Development

###Eclipse
1. mvn -DdownloadSources=true -DdownloadJavadocs=true  eclipse:eclipse
2. Set project properties: Java compliance level 1.8
3. Project settings -> JavaScript -> Include Path -> Excluded: node_modules (due to time consuming validation processes).
4. Start from command line: mvn spring-boot:run or mvn package.
5. You may start for debugging also OpenVisuApplication.java (if this not works try mvn package from command line first).
