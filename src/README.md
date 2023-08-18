# Receipt Processor Web Service

The Receipt Processor Web Service is a Java-based web application built using the Spring Boot framework. It processes receipts and calculates points based on specific rules. The data is stored in memory during the application's runtime.

## Table of Contents
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)

## Project Structure
├── yourpackage/
│ ├── controller/
│ │ ├── ReceiptController.java
│ ├── exception/
│ │ ├── ReceiptNotFoundException.java
│ │ ├── ReceiptProcessingException.java
│ ├── model/
│ │ ├── Receipt.java
│ │ ├── Item.java

## Prerequisites

- Java Development Kit (JDK) 8 or later
- Apache Maven
- IDE (e.g., IntelliJ IDEA, Eclipse)

## Installation

1. Clone this repository:

    ```bash
    git clone https://github.com/yourusername/receipt-processor.git
    cd receipt-processor
    ```

2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

## Usage

1. Run the application:

    ```bash
    mvn spring-boot:run
    ```

2. The application will be accessible at `http://localhost:8080`.

## API Endpoints

1. **Process Receipts**

    - Endpoint: `POST /receipts/process`
    - Payload: JSON receipt data
    - Response: JSON with receipt ID
    - Description: Processes a receipt and returns an ID.

2. **Get Points**

    - Endpoint: `GET /receipts/{id}/points`
    - Response: JSON with awarded points
    - Description: Retrieves the points awarded for a receipt ID.

## Testing

Unit tests are provided to ensure the correctness of the application. To run the tests:

```bash
mvn test
