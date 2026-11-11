package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationControllerTest {

    private static final String JOHN_LENNON_ID = "16a596ae-edd3-4847-99fe-c4518e82c86f";
    private static final String PAUL_MCCARTNEY_ID = "b7839309-3348-463b-a7e3-5de1c168beb3";
    private static final String INVALID_EMPLOYEE_ID = "not-an-employee";

    private String compensationUrl;
    private String compensationAsOfUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/employee/{employeeId}/compensation";
        compensationAsOfUrl =
                "http://localhost:" + port + "/employee/{employeeId}/compensation/{date}";
    }

    @Test
    public void testCreateFindAllAndFindMostRecent() {
        Compensation compensation = new Compensation();
        compensation.setSalary(new BigDecimal("95000"));
        compensation.setEffectiveDate(LocalDate.of(2023, 1, 1));

        ResponseEntity<Compensation> createResponse =
                restTemplate.postForEntity(compensationUrl, compensation, Compensation.class,
                        JOHN_LENNON_ID);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals(JOHN_LENNON_ID, createResponse.getBody().getEmployeeId());

        ResponseEntity<Compensation[]> allResponse =
                restTemplate.getForEntity(compensationUrl, Compensation[].class, JOHN_LENNON_ID);

        assertEquals(HttpStatus.OK, allResponse.getStatusCode());
        assertNotNull(allResponse.getBody());
        assertEquals(4, allResponse.getBody().length);

        ResponseEntity<Compensation> mostRecentResponse = restTemplate.getForEntity(
                compensationUrl + "/mostRecent",
                Compensation.class,
                JOHN_LENNON_ID
        );

        assertEquals(HttpStatus.OK, mostRecentResponse.getStatusCode());
        assertEquals(LocalDate.of(2023, 1, 1),
                mostRecentResponse.getBody().getEffectiveDate());
    }

    @Test
    public void testFindAsOfDateReturnsMostRecentEarlierCompensation() {
        ResponseEntity<Compensation> response = restTemplate.getForEntity(
                compensationAsOfUrl,
                Compensation.class,
                JOHN_LENNON_ID,
                "2022-06-01"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("90000"), response.getBody().getSalary());
        assertEquals(LocalDate.of(2022, 1, 1),
                response.getBody().getEffectiveDate());
    }

    @Test
    public void testEmptyResultsReturnExpectedResponses() {
        ResponseEntity<Compensation[]> allResponse =
                restTemplate.getForEntity(compensationUrl, Compensation[].class, PAUL_MCCARTNEY_ID);

        assertEquals(HttpStatus.OK, allResponse.getStatusCode());
        assertNotNull(allResponse.getBody());
        assertEquals(0, allResponse.getBody().length);

        ResponseEntity<Compensation> asOfResponse = restTemplate.getForEntity(
                compensationAsOfUrl,
                Compensation.class,
                JOHN_LENNON_ID,
                "2019-01-01"
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, asOfResponse.getStatusCode());
    }

    @Test
    public void testIncorrectEmployeeIdsAreRejected() {
        ResponseEntity<String> allResponse =
                restTemplate.getForEntity(compensationUrl, String.class, INVALID_EMPLOYEE_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, allResponse.getStatusCode());

        Compensation compensation = new Compensation();
        compensation.setSalary(new BigDecimal("50000"));
        compensation.setEffectiveDate(LocalDate.of(2024, 1, 1));

        ResponseEntity<String> createResponse =
                restTemplate.postForEntity(compensationUrl, compensation, String.class,
                        INVALID_EMPLOYEE_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createResponse.getStatusCode());
    }
}
