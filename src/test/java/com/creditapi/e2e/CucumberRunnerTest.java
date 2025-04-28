package com.creditapi.e2e;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/com/creditapi/e2e",
    glue = "com.creditapi.e2e",
    plugin = {
        "pretty",
        "json:target/cucumber-report.json",
        "html:target/cucumber-report.html"
    },
    publish = true,
    snippets = CucumberOptions.SnippetType.CAMELCASE
)
@SpringBootTest
@ActiveProfiles("test")
public class CucumberRunnerTest {}
