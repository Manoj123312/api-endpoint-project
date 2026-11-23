# George Travel Cab Booking: Project Report

## 1. Problem and objective

George's travel company needs a simple way to record cab bookings and calculate trip prices. This project demonstrates a REST-based microservice design using Spring Boot, a browser interface, Maven and JUnit. The objective is to create a local, end-to-end educational prototype that can be built, run and tested independently.

## 2. Scope and assumptions

An operator enters the customer's name, pickup and destination, distance in kilometres and cab category. The system shows an estimate, confirms a booking, and lists saved bookings. The operator supplies the distance; the application does not calculate routes. The currency is INR and the tariff is illustrative. There are no users, payments, driver allocation, notifications or live vehicle tracking. It is not production-ready or suitable for sensitive customer data.

## 3. Technology stack

Java 17 or 21; Spring Boot 3.5.7; Spring Web and REST; Jakarta Bean Validation; Spring Data JPA and file-based H2; Maven Wrapper and Maven 3.9.11; JUnit 5, Mockito and Spring MockMvc; HTML, CSS and JavaScript. Postman or curl can exercise the HTTP APIs. Eclipse can import the project as an existing Maven project.

## 4. Architecture

The browser loads static pages from the booking service on port 8080. It sends HTTP requests to that same service. The booking service calls the separate fare service on port 8081 over HTTP before returning a quote or persisting a booking. The fare service is stateless and computes a price; the booking service owns its H2 database. Each service has its own Spring Boot application and executable jar. Inter-service timeouts are configured, and a failed fare call returns 502 without saving a booking.

Flow: Browser -> booking API -> fare API -> calculated quote -> booking database -> browser confirmation and list.

## 5. Fare calculation

Fare = base fare + (distance in km × rate per km), rounded to two decimal places using HALF_UP. The common base is INR 50.00. Rates are INR 12.00 for STANDARD, INR 18.00 for SUV and INR 25.00 for LUXURY. For 12.5 km in a STANDARD cab, fare = 50 + 12.5 × 12 = INR 200.00. Decimal arithmetic is used instead of floating-point arithmetic.

## 6. API specification

POST /api/fares/quote on port 8081 accepts distanceKm and cabType and returns distance, category, base fare, rate, total and currency. POST /api/fare-quote on port 8080 proxies the calculation. POST /api/bookings on port 8080 accepts customerName, pickup, destination, distanceKm and cabType; on success it returns HTTP 201 and the saved record. GET /api/bookings lists records newest first; GET /api/bookings/{id} returns one record or 404.

Distance must be 0.1 to 10000 inclusive with at most two decimal places. Names and locations must not be blank and are length-limited. Unknown cab types and malformed input return 400. Failure to contact the fare service or an invalid quote returns 502. The server independently obtains the fare when a booking is submitted; the client cannot set the price.

## 7. Data model

Booking stores an auto-generated numeric ID, customer name, pickup, destination, distance, cab type, total fare, currency and creation time in UTC. H2 persists data on disk relative to the booking process working directory. The booking service uses Spring Data JPA; no separate database installation is required.

## 8. Build, run and local installation

Install a JDK 17 or 21 and Git. Clone the repository or download and extract its ZIP. From the project root run ./mvnw clean verify (Windows: mvnw.cmd clean verify). Start the fare jar first, then the booking jar in another terminal. Open http://localhost:8080/. The README provides complete commands, Eclipse import instructions, configuration, Postman examples and troubleshooting.

## 9. Testing strategy

The fare controller's MockMvc tests cover all three tariffs and invalid input. Booking controller tests cover successful creation from a server-side fare, validation failures, a failed fare service without persistence, and an unknown ID. The Maven verify command compiles, runs tests and packages both executable jars. Manual end-to-end testing covers the live inter-service request, database persistence, fare estimate, booking submission and display. Postman can repeat the JSON requests documented in the README. A separate production-quality system would additionally need security, concurrency, load, resilience and deployment testing.

## 10. Outcome and limitations

The prototype demonstrates independent services, REST controllers, validation, persistent booking data, error handling, browser interaction and repeatable packaging. The list is unpaginated and intended only for small demos. H2 and automatic schema updates are convenient for learning, not an operational production strategy. Authentication, authorization, audit, backup, monitoring, migrations, privacy safeguards, resilient deployment and real-time transport integration are future work.
