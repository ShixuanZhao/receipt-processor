package com.shixuanzhao.receipt_processor;

import com.shixuanzhao.receipt_processor.Controller.ReceiptController;
import com.shixuanzhao.receipt_processor.Exception.CustomExceptionHandler;
import com.shixuanzhao.receipt_processor.Model.Receipt;
import com.shixuanzhao.receipt_processor.Model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptControllerTest {

    @InjectMocks
    private ReceiptController receiptController;

    @Mock
    private Map<String, Receipt> receiptMap;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(receiptController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    public void testProcessReceipt() {
        MockitoAnnotations.initMocks(this);

        // Create a sample receipt for testing
        Date purchaseDate = parseDate("2022-01-01");

        List<Item> items = new ArrayList<>();
        items.add(new Item("Mountain Dew 12PK", 6.49));
        items.add(new Item("Emils Cheese Pizza", 12.25));
        items.add(new Item("Knorr Creamy Chicken", 1.26));
        items.add(new Item("Doritos Nacho Cheese", 3.35));
        items.add(new Item("   Klarbrunn 12-PK 12 FL OZ  ", 12.00));

        Receipt receipt = new Receipt("Target", purchaseDate, "13:01", items, 35.35);

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

        // Create a sample receipt for testing
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date purchaseDate = parseDate("2022-01-01");

        List<Item> items = new ArrayList<>();
        items.add(new Item("Mountain Dew 12PK", 6.49));
        items.add(new Item("Emils Cheese Pizza", 12.25));
        items.add(new Item("Knorr Creamy Chicken", 1.26));
        items.add(new Item("Doritos Nacho Cheese", 3.35));
        items.add(new Item("   Klarbrunn 12-PK 12 FL OZ  ", 12.00));

        Receipt receipt = new Receipt("Target", purchaseDate, "13:01", items, 35.35);

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

        // Create a sample receipt for testing
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date purchaseDate = parseDate("2022-01-01");

        List<Item> items = new ArrayList<>();
        items.add(new Item("Mountain Dew 12PK", 6.49));
        items.add(new Item("Emils Cheese Pizza", 12.25));
        items.add(new Item("Knorr Creamy Chicken", 1.26));
        items.add(new Item("Doritos Nacho Cheese", 3.35));
        items.add(new Item("   Klarbrunn 12-PK 12 FL OZ  ", 12.00));

        Receipt receipt = new Receipt("Target", purchaseDate, "13:01", items, 35.35);

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
