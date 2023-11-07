## Building the app

```bash
mvn clean package
```
will compile the app, run the unit tests and produce an uber-jar in the target folder.

## Running the app
You will need a running postgres instance that you can spin up with docker-compose.

```bash
docker compose -f docker/docker-compose.yml up -d
```

You may stop the instance once you're finished.
```bash
docker compose -f docker/docker-compose.yml down
```

Once postgres is up, run the app itself

```bash
java -jar target/news-api-0.0.1-SNAPSHOT.jar
```
will run the app with empty list of contacts.

```bash
java -Dapp.contacts.generate -jar target/news-api-0.0.1-SNAPSHOT.jar
```
will run it with some random contacts in the system.

Open a new browser tab and access the following URL: http://localhost:8080

## Available commands

The app allows viewing, adding, editing and removing contacts. They are stored in a table in postgres.

## Configuration

The app reads its configuration from the `src/main/resources/application.yml` file.
You may override any of the parameters from the command line using the `-D` flag similar to the examples above.

The following configuration parameters govern the behavior of the app

