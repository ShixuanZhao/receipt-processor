package com.shixuanzhao.receipt_processor.Controller;

import com.shixuanzhao.receipt_processor.Exception.ReceiptNotFoundException;
import com.shixuanzhao.receipt_processor.Exception.ReceiptProcessingException;
import com.shixuanzhao.receipt_processor.Model.Item;
import com.shixuanzhao.receipt_processor.Model.Receipt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final Map<String, Receipt> receiptMap = new HashMap<>();

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processReceipt(@RequestBody Receipt receipt) {
        if (receipt == null) {
            throw new ReceiptProcessingException("Receipt cannot be null");
        }
        String id = UUID.randomUUID().toString();
        receiptMap.put(id, receipt);
        return ResponseEntity.ok(Collections.singletonMap("id", id));
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<Map<String, Integer>> getPoints(@PathVariable String id) {
        Receipt receipt = receiptMap.get(id);
        if (receipt == null) {
            throw new ReceiptNotFoundException("Receipt not found with ID: " + id);
        }

        int points = calculatePoints(receipt);
        return ResponseEntity.ok(Collections.singletonMap("points", points));
    }

    // Calculate points based on the provided rules
    public int calculatePoints(Receipt receipt) {
        int points = 0;

        // Rule 1: One point for every alphanumeric character in the retailer name
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule 2: 50 points if the total is a round dollar amount with no cents
        if (receipt.getTotal() == (int) receipt.getTotal()) {
            points += 50;
        }

        // Rule 3: 25 points if the total is a multiple of 0.25
        if (receipt.getTotal() % 0.25 == 0) {
            points += 25;
        }

        // Rule 4: 5 points for every two items on the receipt
        points += receipt.getItems().size() / 2 * 5;

        // Rule 5: If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer.
        // The result is the number of points earned.
        for (Item item : receipt.getItems()) {
            if (item.getShortDescription().trim().length() % 3 == 0) {
                int extraPoints = (int) Math.ceil(item.getPrice() * 0.2);
                points += extraPoints;
            }
        }

        // Rule 6: 6 points if the day in the purchase date is odd
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(receipt.getPurchaseDate());
        if (calendar.get(Calendar.DAY_OF_MONTH) % 2 != 0) {
            points += 6;
        }

        // Rule 7: 10 points if the time of purchase is after 2:00pm and before 4:00pm
        calendar.setTime(receipt.getPurchaseDate());
        int hourOfDay = Integer.parseInt(receipt.getPurchaseTime().split(":")[0]);
        int minute = Integer.parseInt(receipt.getPurchaseTime().split(":")[1]);
        if (hourOfDay == 14 && minute >= 0 || hourOfDay > 14 && hourOfDay < 16) {
            points += 10;
        }

        return points;
    }
}


