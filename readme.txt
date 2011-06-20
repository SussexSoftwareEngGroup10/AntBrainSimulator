The source code has been split into five packages, all held in the src folder.
The main class is mainWindow; the code is built and run from that class.
javadoc for all these classes is held in the doc folder.  Open index.html
to access this.

Test classes for other classes are suffexed with "test".  They are not run
automatically every time the program is compiled because together the whole
test suite can take around 4 minutes to run.  For this reason, they are run
manually when significant changes to the code are made or new functionality is
added.
The tests are documented in the test specification, and not th  javadoc.

brain_populations contains a serialized version of the GeneticAlgorithm class. 
This is done so the program can read and write them to save their state so they 
can be run again later and the same data can be retrieved.

brains contains a selection of .ant files that can be run with this program.

changelog contains a log file of all commits to the svn repository since the
project was created.

lib contains two third party library files used when drawing the processing 
sketch.
core.jar is from http://processing.org/
gicentreUtils is from http://www.gicentre.org/utils/

logs contains log files created when the program is run.

resources contains all the additional files used by the program (all images, 
font and sound files).

worlds contains various worlds that can be uploaded to the running program.