# CRM Service

CRM Service is a backend service for `Customers` and `Users` management.

It is composed by two sets of APIs to handle CRUD operations over customers and users. 

All endpoints are protected by Basic Auth, which means the user is required to provide username and password for use.

Users can manage `Customers`, while an `admin` user can manage `Customers` and `Users`.

## Tech Stack

* Java
* Spring Boot 3
* Maven
* Postgres
* Open API
* Testcontainers

This project uses TeamCity as CI/CD tool, after a new commit at main branch, the build pipeline is triggered, tests are executed
and a new docker image is build and sent to AWC ECR.

## Building and Running

### Requisites

* Java JDK 17
* Docker (for running tests with Testcontainers)
* Docker Compose (optional)
* Postgres 14.3 (optional)

### Building Steps
* Checkout the repository.
* Ensure you have a docker environment running.
* Run `.mvnw clean install -U` to download dependencies and build the application jar.

### Running Options

#### Docker Compose 

Use the environment variables below to configure a specific user created to access the ECR repository that is storing the application docker image.
* `export AWS_ACCESS_KEY_ID=AKIA4WZUMZBQOMUDWNJU`
* `export AWS_SECRET_ACCESS_KEY=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`
* `export AWS_DEFAULT_REGION=us-east-1`

Log in the AWS ECR using provided credentials
* aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 873598863456.dkr.ecr.us-east-1.amazonaws.com

Running `docker compose up -d`, it will:

* Pull the `postgres:14.3` image from Docker Hub and start it.
* Pull the `crm-service:latest` image from ECR.
* Run the CRM Service application

The application will start using the Spring "default" profile and there is no additional settings required.

#### IDE or Command Line (terminal)

To run the application through an IDE or terminal, make sure you are using the `local` Spring profile.

* Terminal: run `export SPRING_PROFILES_ACTIVE=local` or `java -jar -Dspring.profiles.active=local target/crmservice-0.0.1-SNAPSHOT.jar`
* IDE: Find in your IDE the option to declare Environment Variables and add `SPRING_PROFILES_ACTIVE=local`

In this option, you can use either the database from docker-compose.yml or install Postgres directly in your machine.

## Documentation

After running the application, open `http://localhost:8080/swagger-ui/index.html` to access the Swagger documentation.

## Postman Collection

To facilitate the interaction with the APIs, there is a Postman collection under `/src/test/resources/postman` folder.

# Future Improvements

* Implement a new class for IUploadService interface, to store the user photo in a S3 bucket (or any cloud storage).

* Enhance CI/CD process by:
* * Adding project artifact versioning process.
* * Add a tagging image process for releases and environments.