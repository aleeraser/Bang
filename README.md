# Bang!

Distributed implementation of the board game "Bang!", using Java and its RMI APIs. The implemented version is slightly different compared to the original game.

## Starting the game

Since the game is based on Java, you will need a [working JRE environment](http://www.oracle.com/technetwork/java/javase/overview/index.html "Oracle Java official page"). To start the game using the last build, simply run:

`$ java -jar ./desktop/build/libs/desktop-1.0.jar`

### Building from source

If you wish, you can also build the game yourself. To do so, you will need [gradle](https://gradle.org/install/ "install gradle").

After cloning the repository, cd into the cloned folder and start the gradle build with:

`$ ./gradlew desktop:dist`

This will build a `desktop-1.0.jar` file inside `./desktop/build/libs/`.

You can also directly build&run using:

`$ ./gradlew desktop:run`


## Authors

Alessandro Zini, Carlo Stomeo, Filippo Morselli