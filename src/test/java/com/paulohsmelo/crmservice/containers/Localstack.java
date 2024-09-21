package com.paulohsmelo.crmservice.containers;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@ActiveProfiles(value = "local")
public class Localstack {

    protected static final String TEST_BUCKET_NAME = "bucket-test";

    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.13.0"))
            .withServices(S3);

    static {
        localStack.start();
    }

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("application.localstack.endpoint", () -> localStack.getEndpointOverride(S3));
        registry.add("application.localstack.region", localStack::getRegion);
        registry.add("application.localstack.access-key", localStack::getAccessKey);
        registry.add("application.localstack.secret-key", localStack::getSecretKey);
        registry.add("application.s3.bucket-name", () -> TEST_BUCKET_NAME);
    }

}
