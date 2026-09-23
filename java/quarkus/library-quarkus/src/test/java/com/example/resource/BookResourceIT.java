package com.example.resource;

import io.quarkus.test.junit.QuarkusIntegrationTest;

/**
 * Runs the same {@link BookResourceTest} suite against the packaged application.
 */
@QuarkusIntegrationTest
class BookResourceIT extends BookResourceTest {
}
