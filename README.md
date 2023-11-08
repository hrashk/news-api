## Building the app

```bash
mvn clean package
```
will compile the app, run the unit tests and produce an uber-jar in the target folder.

## Running with maven

The following command will run the app from the compiled sources.
You'll need a running postgres db as described in the next section.
```bash
mvn spring-boot:run
```

The following command will run a version of the application with a postgres db as a testcontainer.
It will have sample news and comments created in the db.
```bash
mvn spring-boot:test-run
```

The following URL shows the available REST API end points and example of their usage: http://localhost:8080
You may interact with the API via Postman or another similar tool.

## Running from jar
You will need a running postgres instance that you can spin up with docker-compose.

```bash
docker compose -f docker/docker-compose.yml up -d
```

You may stop the instance once you're finished.
```bash
docker compose -f docker/docker-compose.yml down
```

Once postgres is up, run the app itself:

```bash
java -jar target/news-api-0.0.1-SNAPSHOT.jar
```

## Available commands

The rest api services allow viewing, adding, editing and removing users, news, comments and categories.
All of the entities are stored in a postgres db.

## Configuration

The app reads its configuration from the `src/main/resources/application.yml` file.
You may override any of the parameters from the command line using the `-D` flag similar to the examples above.

The following configuration parameters govern the behavior of the app
