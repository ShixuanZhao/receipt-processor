package com.shixuanzhao.receipt_processor;

import com.shixuanzhao.receipt_processor.Controller.ReceiptController;
import com.shixuanzhao.receipt_processor.Model.Item;
import com.shixuanzhao.receipt_processor.Model.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptControllerTest {

    @InjectMocks
    private ReceiptController receiptController;

    private Receipt receipt;

    @BeforeEach
    public void setup() {
        // Create a sample receipt for testing
        Date purchaseDate = parseDate("2022-01-01");
        List<Item> items = new ArrayList<>();
        items.add(new Item("Mountain Dew 12PK", 6.49));
        items.add(new Item("Emils Cheese Pizza", 12.25));
        items.add(new Item("Knorr Creamy Chicken", 1.26));
        items.add(new Item("Doritos Nacho Cheese", 3.35));
        items.add(new Item("   Klarbrunn 12-PK 12 FL OZ  ", 12.00));
        receipt = new Receipt("Target", purchaseDate, "13:01", items, 35.35);
    }

    @Test
    public void testProcessReceipt() {
        MockitoAnnotations.initMocks(this);

        ResponseEntity<Map<String, String>> response = receiptController.processReceipt(receipt);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("id"));
    }

    @Test
    public void testGetPoints() {
        MockitoAnnotations.initMocks(this);

        // Call ProcessReceipt to get an ID
        ResponseEntity<Map<String, String>> processResponse = receiptController.processReceipt(receipt);

        assertNotNull(processResponse);
        assertEquals(200, processResponse.getStatusCodeValue());
        Map<String, String> processResponseBody = processResponse.getBody();
        assertNotNull(processResponseBody);
        assertTrue(processResponseBody.containsKey("id"));

        String generatedId = processResponse.getBody().get("id");
        // Call GetPoints with the generated ID
        ResponseEntity<Map<String, Integer>> response = receiptController.getPoints(generatedId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Integer> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("points"));

        // Define the expected points based on the provided example
        int expectedPoints = 6 + 10 + 3 + 3 + 6;
        assertEquals(expectedPoints, responseBody.get("points"));
    }

    @Test
    public void testCalculatePoints() {
        MockitoAnnotations.initMocks(this);

        int points = receiptController.calculatePoints(receipt);

        // Define the expected points based on the provided example
        int expectedPoints = 6 + 10 + 3 + 3 + 6;
        assertEquals(expectedPoints, points);
    }

    // Helper method to parse a date from a string
    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date: " + dateString, e);
        }
    }
}
