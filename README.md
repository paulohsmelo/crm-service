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
* AWS S3
* Testcontainers (for tests)
* Localstack (for tests and local environment)

## Running Instance

There is a running instance available in AWS, currently the URL to access is: http://35.175.105.159:8080/

This instance is deployed using ECS and Fargate, pulling the docker image from ECR.

Check ´Postman Collection` section to facilitate the interaction with the endpoints.

## Running in Local Machine

### Requisites

* Java JDK 17
* Docker (for running tests with Testcontainers)
* Docker Compose (optional)
* Postgres 14.3 (optional)
* Localstack (optional)

### Docker Compose 

Request AWS credentials and export them in your terminal, example:
* `export AWS_ACCESS_KEY_ID=*****`
* `export AWS_SECRET_ACCESS_KEY=******`
* `export AWS_DEFAULT_REGION=******`

Log in the AWS ECR using provided credentials
* aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 873598863456.dkr.ecr.us-east-1.amazonaws.com

Running `docker compose up -d`, it will:

* Pull the `postgres:14.3` image from Docker Hub and start it.
* Pull the `localstack:latest` image from Docker Hub and start it.
* Pull the `crm-service:latest` image from ECR.
* Run the CRM Service application

The application will start using the Spring "local" profile and there is no additional settings required.

### IDE or Command Line (terminal)

To run the application through an IDE or terminal, make sure you are declaring all necessary environment variables.

Use `.env` file to run using `local` Spring profile.

* Terminal: run `java -jar -Dspring.profiles.active=local target/crmservice-0.0.1-SNAPSHOT.jar`
* IDE: Find in your IDE the option to declare Environment Variables (or file variables).

With this option, you can use either the database and localstack from docker-compose.yml, or install Postgres and localstack directly in your machine.

## Documentation

After running the application, open `http://localhost:8080/swagger-ui/index.html` to access the Swagger documentation.

A default user admin is created by database migration, username `admin`, password `admin123`.

## CI/CD

This project uses TeamCity as CI/CD tool, after a new commit at main branch, the build pipeline is triggered, tests are executed
and a new docker image is build and pushed to AWS ECR.

Request TeamCity credentials and access https://crmservice.teamcity.com/project/CrmService?mode=builds to check the build.

Alternatively, access the repository in GitHub and check the build for individual commits in the `Commit List`. 

## Postman Collection

To facilitate the interaction with the APIs, there is a Postman collection under `/src/test/resources/postman` folder.

Create a new `crm-url` variable and populate with either `http://35.175.105.159:8080` for interact with AWS instance or 
`http://localhost:8080` for localhost instance.

# Future Improvements

* Implement a new class for IUploadService interface, to store the user photo in a S3 bucket (or any cloud storage).

* Enhance CI/CD process by:
* * Adding project artifact versioning process.
* * Add a tagging image process for releases and environments.
* * Add a load balancer and DNS to a specific domain.
* * Eventually move the deployment process to EKS.