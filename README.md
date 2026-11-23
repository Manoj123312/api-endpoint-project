# George Travel: cab booking microservices

A runnable educational Spring Boot application with two separate REST services: a fare calculator and a booking service. The booking service serves the web page, requests a fare from the fare service, and saves bookings in a local H2 database. This is a demo, not a production dispatch or payment system.

## Requirements

- JDK 17 or 21 (not just a JRE); check with `java -version`.
- Internet access on the first build for Maven and dependencies. Maven itself is downloaded by the included wrapper; a separate Maven install is not required.
- Git, or download the repository as a ZIP from GitHub.
- Optional: Eclipse IDE for Enterprise Java and Web Developers and Postman.
- Ports 8080 and 8081 must be available.

## Download and build

1. Install a JDK from [Eclipse Temurin](https://adoptium.net/temurin/releases/) and set `JAVA_HOME` to its installation directory if needed. Open a new terminal and run `java -version`.
2. Install [Git](https://git-scm.com/downloads), then run:

   ```sh
   git clone https://github.com/Manoj123312/api-endpoint-project.git
   cd api-endpoint-project
   ```

   Alternatively, choose **Code > Download ZIP** on GitHub, extract it, and open a terminal in the extracted folder.
3. Build both executable jars and run the tests:

   ```sh
   ./mvnw clean verify
   ```

   On Windows Command Prompt or PowerShell, use `mvnw.cmd clean verify` instead. On macOS/Linux, if necessary run `chmod +x mvnw` first. The first run downloads Maven and dependencies.

## Run locally

Open two terminals in the project root. Start the fare service first:

```sh
java -jar fare-service/target/fare-service-1.0.0.jar
```

Wait for its `Started FareApplication` message, then start the booking service in the second terminal:

```sh
java -jar booking-service/target/booking-service-1.0.0.jar
```

Wait for `Started BookingApplication`, then open **http://localhost:8080/**. Enter a name, pickup, destination, distance and cab category. Select **Estimate fare**, then **Confirm booking**. The confirmed trip appears under Recent bookings. Stop each process with Ctrl+C.

Bookings are stored in `data/bookings.mv.db` relative to the booking service's current working directory. Restarting from the same directory preserves them. Back up this file only after stopping the service. Deleting it after stopping the service removes the demo bookings.

### Eclipse

1. Install Eclipse IDE for Enterprise Java and Web Developers and configure the JDK under **Window > Preferences > Java > Installed JREs**.
2. Select **File > Import > Maven > Existing Maven Projects**, choose the extracted/cloned root directory, and import the parent and both modules. Wait for dependencies to resolve.
3. Run `FareApplication` as a Java application, wait for startup, then run `BookingApplication`. Open the URL above. Run tests with **Run As > JUnit Test**, or use the wrapper from a terminal for the complete build.

## API and Postman

Create a Postman request with `Content-Type: application/json`, select **raw > JSON**, and send the following. Alternatively, the examples work in a Unix-like terminal with `curl`.

**Fare service:** `POST http://localhost:8081/api/fares/quote`

```json
{"distanceKm":12.5,"cabType":"STANDARD"}
```

Response: HTTP 200, with `baseFare: 50.00`, `ratePerKm: 12.00`, `totalFare: 200.00`, and `currency: "INR"`. The booking service also exposes `POST http://localhost:8080/api/fare-quote` with the same body, for use by its web page.

**Create booking:** `POST http://localhost:8080/api/bookings`

```json
{"customerName":"Alex Morgan","pickup":"Central Station","destination":"Airport","distanceKm":12.5,"cabType":"STANDARD"}
```

Response: HTTP 201 and a booking with its generated `id`, fare, currency and UTC `createdAt`. The fare is calculated by the fare service, not accepted from the client.

**List bookings:** `GET http://localhost:8080/api/bookings`; **find one:** `GET http://localhost:8080/api/bookings/1` (replace 1 with a returned ID). An unknown ID returns 404.

```sh
curl -H "Content-Type: application/json" -d '{"distanceKm":12.5,"cabType":"STANDARD"}' http://localhost:8081/api/fares/quote
curl -H "Content-Type: application/json" -d '{"customerName":"Alex Morgan","pickup":"Central Station","destination":"Airport","distanceKm":12.5,"cabType":"STANDARD"}' http://localhost:8080/api/bookings
curl http://localhost:8080/api/bookings
```

Valid cab types are `STANDARD`, `SUV`, and `LUXURY`. Distance is 0.1–10000 km with at most two decimal places. Name is required and at most 100 characters; pickup and destination are required and at most 200 characters. Invalid JSON or fields return 400; unavailable or invalid fare service responses return 502 and no booking is saved.

## Configuration and troubleshooting

Both services honor `PORT` (defaults 8081 for fare and 8080 for booking). Booking honors `FARE_SERVICE_URL` (default `http://localhost:8081`) and `DATABASE_URL` (default `jdbc:h2:file:./data/bookings`). Set environment variables separately in the relevant terminal before starting a service. For example, on macOS/Linux: `PORT=8082 java -jar fare-service/target/fare-service-1.0.0.jar` and `FARE_SERVICE_URL=http://localhost:8082 java -jar booking-service/target/booking-service-1.0.0.jar`. In Windows PowerShell use `$env:PORT="8082"` or `$env:FARE_SERVICE_URL="http://localhost:8082"` before the corresponding command.

If a port is already in use, stop the conflicting program or change the relevant port and URL. If booking reports a 502, ensure the fare service is running and its URL is correct. If Maven cannot download dependencies, check your network/proxy. If H2 reports a locked database, stop the other booking process using that same database. Run both services from the project root to use the expected database location.

## Project documents

See `docs/REPORT.md` for the project report. The complete application source, tests and build files are in the repository.

## Limitations

This demonstration has no authentication, authorization, privacy controls, driver assignment, maps, payments, cancellation, real-time driver tracking, or concurrent-user scaling. The distance is entered by the user; prices are illustrative INR amounts, not a commercial tariff. Do not expose it publicly or use it for real customer data without production security, database migrations, monitoring, rate limits, and operational hardening.
