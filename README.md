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

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    npm install -g gulp

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.


## Building for production

To optimize the OpenViSu client for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify CSS and JavaScript files. It will also modify `index.html` so it references
these new files.

To ensure everything worked, run:

    java -jar target/*.war --spring.profiles.active=prod

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Testing

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript/` and can be run with:

    gulp test



## Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `OpenViSu`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/OpenViSu.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
