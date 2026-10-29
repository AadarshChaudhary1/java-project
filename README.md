# Everyday Calculators

A browser-based version of the original Java calculator, powered by Spring Boot and architected for both standard containerized deployment and serverless deployment to **AWS Lambda with a public Function URL** under the **AWS Free plan**.

The application serves a modern, responsive web frontend and JSON REST API from the same process or serverless handler. Calculations execute in-memory on the backend and do not persist user data.

---

## Features

- **BMI Calculator:** Metric height and weight calculations with standard clinical classification thresholds (`Underweight`, `Normal weight`, `Overweight`, and `Obesity`).
- **Mathematical Calculator:** Operations include basic arithmetic (`ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`), power (`POWER`), square root (`SQRT`), factorial (`FACTORIAL` up to 170), modulus (`MODULUS`), trigonometric functions (`SIN`, `COS`, `TAN` using degrees), logarithms (`LOG10`, `LN`), and absolute value (`ABS`).
- **Number Guessing Game:** In-memory secret number game (range 1–100) with higher/lower hints, attempt counting, and session state tracking.
- **Unit Converter:** Conversions for length, weight, temperature, volume, and area using standard physical conversion ratios.
- **Area Calculator:** Geometric area for Circle, Rectangle, Triangle, and Square.
- **Volume Calculator:** Geometric volume for Sphere, Cube, Cylinder, Cuboid, and Cone.
- **Health Check Endpoint:** `GET /api/health` returning `{"status":"UP"}`.
- **Input Validation:** Client-side constraint enforcement and server-side Jakarta Bean Validation returning structured JSON error messages (`{"error":"..."}`).

The original [`Project.java`](./Project.java) console application source and its compiled `Project.class` are retained for historical reference. The Maven build compiles the Spring Boot application from `src/main/java`.

---

## Architectural Design

### 1. Request Flow

```text
Local / Container Execution:
Browser (HTML/CSS/JS)
  ├── GET /                 --> Embedded Tomcat --> ResourceHandler (index.html)
  ├── GET /styles.css       --> Embedded Tomcat --> ResourceHandler (styles.css)
  ├── GET /app.js           --> Embedded Tomcat --> ResourceHandler (app.js)
  └── POST /api/...         --> Embedded Tomcat --> DispatcherServlet --> Controllers --> Services

AWS Serverless Execution:
Browser (HTTPS)
  │
  ▼
AWS Lambda Function URL (Payload v2.0)
  │
  ▼
StreamLambdaHandler (AWS Serverless Java Container)
  │
  ▼
Spring Boot Application Context (In-Memory DispatcherServlet)
  ├── WelcomePageHandlerMapping / ResourceHttpRequestHandler (HTML, CSS, JS)
  └── REST Controllers (@RestController) --> Calculator Services (@Service)
```

### 2. Technology Stack

- **Language & Runtime:** Java 25 (LTS)
- **Framework:** Spring Boot 3.5.3 (Spring MVC, Jakarta Bean Validation)
- **Serverless Adapter:** AWS Serverless Java Container (`aws-serverless-java-container-springboot3` v2.1.6)
- **Build & Packaging:** Apache Maven (`spring-boot-maven-plugin` and `maven-shade-plugin`)
- **Frontend:** Vanilla HTML5, CSS3 (CSS custom properties, CSS grid/flexbox), ES6+ JavaScript (Fetch API)
- **Containerization:** Multi-stage Dockerfile based on Eclipse Temurin 25 Alpine
- **CI/CD:** GitHub Actions (Maven verify, Docker build, Lambda artifact generation)

---

## Project Structure

```text
java-project/
├── .github/
│   └── workflows/
│       ├── ci.yml                 # Automated build, test, Docker check, Lambda packaging
│       └── aws.yml                # Retained ECS deployment template (manual dispatch)
├── .aws/
│   └── task-definition.json       # Retained ECS task definition template
├── src/
│   ├── main/
│   │   ├── java/com/example/calculator/
│   │   │   ├── CalculatorApplication.java     # Spring Boot application entrypoint
│   │   │   ├── StreamLambdaHandler.java       # AWS Lambda Function URL stream handler
│   │   │   ├── controller/
│   │   │   │   ├── ApiExceptionHandler.java   # Centralized error and validation handling
│   │   │   │   ├── BmiController.java         # /api/bmi endpoint
│   │   │   │   ├── ConversionController.java  # /api/conversions endpoint
│   │   │   │   ├── GeometryController.java    # /api/geometry/area & volume endpoints
│   │   │   │   ├── GuessingGameController.java# /api/games/guess/... endpoints
│   │   │   │   ├── HealthController.java      # /api/health endpoint
│   │   │   │   └── MathController.java        # /api/math endpoint
│   │   │   └── service/
│   │   │       ├── BmiService.java
│   │   │       ├── ConversionService.java
│   │   │       ├── GeometryService.java
│   │   │       ├── GuessingGameService.java
│   │   │       └── MathService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html     # Responsive calculator UI
│   │           ├── app.js         # Client-side dynamic interaction & API calls
│   │           └── styles.css     # Responsive typography and card styling
│   └── test/
│       └── java/com/example/calculator/
│           ├── CalculatorApiTest.java         # Spring MockMvc integration tests
│           ├── StreamLambdaHandlerTest.java   # Serverless Lambda handler end-to-end tests
│           └── service/
│               └── CalculatorServiceTest.java # Pure business logic unit tests
├── Dockerfile                     # Multi-stage production container build
├── pom.xml                        # Maven configuration with dual packaging profiles
└── README.md
```

---

## Local Development & Execution

### Prerequisites
- JDK 25 (LTS)
- Apache Maven 3.9+
- Docker Desktop (optional, for container runs)

### Run locally via Spring Boot
```bash
mvn spring-boot:run
```
Or build and run the executable jar:
```bash
mvn clean package
java -jar target/java-project-1.0.0.jar
```
Open [http://localhost:8080](http://localhost:8080) in your browser.

### Docker execution
```bash
docker build -t everyday-calculators .
docker run --rm -p 8080:8080 everyday-calculators
```

---

## Automated Testing

Execute the complete test suite (unit tests, Spring MVC API tests, and AWS Lambda handler tests):
```bash
mvn clean verify
```

To run tests and produce both the standalone executable JAR and the shaded AWS Lambda package:
```bash
mvn clean verify -P lambda
```

Artifacts generated in `target/`:
- `java-project-1.0.0.jar`: Standard executable Spring Boot JAR with embedded Tomcat (for local/Docker).
- `java-project-1.0.0-lambda.jar`: Shaded, optimized flat JAR with Tomcat excluded and AWS Serverless Java Container attached (for AWS Lambda).

---

## AWS Deployment Architecture (Free Plan Compatible)

### Why AWS Lambda + Function URL?

1. **Strict Cost Control ($0 Idle Cost):**
   - Traditional container services (ECS Fargate) run continuously. Even a minimal 0.25 vCPU task costs ~$10–$15/month in compute plus hourly charges for public IPv4 addresses ($0.005/hour ≈ $3.60/month). Adding an Application Load Balancer costs ~$16–$22/month.
   - **AWS Lambda scales to zero.** When no requests are being processed, compute consumption is zero.
   - The AWS Free Tier provides **1,000,000 free requests per month** and **3.2 million compute seconds** (400,000 GB-seconds) indefinitely.
2. **Built-in HTTPS Endpoint:**
   - Lambda Function URLs provide an out-of-the-box secure HTTPS endpoint (`https://<unique-id>.lambda-url.<region>.on.aws/`) with free AWS-managed TLS certificates.
   - No Application Load Balancer, API Gateway, or CloudFront distribution is strictly required for the core application to function.
3. **No IAM Identity Provider / OIDC Dependency:**
   - Accounts on the Free plan displaying *"Feature unavailable — To use this feature, activate advanced features"* cannot configure GitHub Actions OIDC identity providers.
   - The Lambda deployment package (`java-project-1.0.0-lambda.jar`) is built by GitHub Actions and uploaded manually via the standard AWS Management Console in seconds without any account upgrades or dangerous long-lived access keys.
4. **Preserves 100% of the Existing Application:**
   - Spring Boot controllers, services, bean validation, custom exceptions, and static assets (`index.html`, `styles.css`, `app.js`) run identically through `StreamLambdaHandler`.

---

## Step-by-Step AWS Console Deployment Guide

Follow these exact steps in the [AWS Management Console](https://console.aws.amazon.com/):

### Step 1: Download the Deployment Package
1. Open your GitHub repository in the browser: `https://github.com/AadarshChaudhary1/java-project`.
2. Navigate to **Actions** -> select the latest run of **Java CI**.
3. Under the **Artifacts** section at the bottom, download `java-calculator-lambda-package`.
4. Unzip the downloaded archive to retrieve `java-project-1.0.0-lambda.jar`.

### Step 2: Create the Lambda Function
1. Log in to the [AWS Management Console](https://console.aws.amazon.com/).
2. Select your desired AWS Region in the top-right navbar (e.g., `us-east-1` N. Virginia or `ap-south-1` Mumbai).
3. Search for and open **Lambda**.
4. Click **Create function**.
5. Select **Author from scratch**.
6. Configure the basic information:
   - **Function name:** `everyday-calculator`
   - **Runtime:** `Java 25` (or `Java 21` if your console defaults to 21)
   - **Architecture:** `x86_64`
7. Expand **Advanced settings**:
   - Check **Enable function URL**.
   - **Auth type:** Select `NONE` (this allows public browser access to your calculator website).
   - Check **Configure cross-origin resource sharing (CORS)** (optional, defaults are fine as frontend and API are same-origin).
8. Click **Create function**.

### Step 3: Upload the Package & Set the Handler
1. On the function page, scroll to the **Code source** section.
2. Click **Upload from** -> select **.zip or .jar file**.
3. Click **Upload**, select the `java-project-1.0.0-lambda.jar` file from Step 1, and click **Save**.
4. Scroll down to the **Runtime settings** section and click **Edit**.
5. Change **Handler** to:
   ```text
   com.example.calculator.StreamLambdaHandler::handleRequest
   ```
6. Click **Save**.

### Step 4: Adjust Memory & Timeout
1. Navigate to the **Configuration** tab -> select **General configuration** -> click **Edit**.
2. **Memory:** Set to `1024 MB` (provides optimal JVM startup speed within the Free Tier allowance).
3. **Timeout:** Set to `30 seconds` (to accommodate cold-start Spring context loading).
4. Click **Save**.

### Step 5: Test the Website
1. Go back to the function overview.
2. Locate the **Function URL** (format: `https://<random-id>.lambda-url.<region>.on.aws/`).
3. Click the link to open it in any desktop or mobile browser.
4. Verify the following:
   - The homepage loads with full styling and fonts.
   - Test BMI calculation: enter 70 kg, 1.75 m -> should output `BMI: 22.9 · Normal weight`.
   - Test Math calculation: 2 + 3 -> should output `Result: 5`.
   - Test Health endpoint: append `/api/health` to the URL -> should output `{"status":"UP"}`.

---

## Monitoring, Cost Control & Cleanup

### Cost Risks & Safeguards
- **Scale-to-Zero:** Lambda only bills for active execution time (rounded to the nearest millisecond). Zero visitors = $0 compute charges.
- **Data Transfer:** AWS Free Tier includes 100 GB of outbound data transfer per month.
- **CloudWatch Logs:** Lambda streams logs to `/aws/lambda/everyday-calculator`. The Free Tier includes 5 GB of log ingestion. To avoid lingering log storage costs:
  - In CloudWatch Console -> **Log groups** -> click `/aws/lambda/everyday-calculator` -> **Actions** -> **Edit retention setting** -> change from *Never expire* to **7 days** or **1 day**.

### How to Clean Up and Avoid Any Future Charges
If you ever wish to decommission the website:
1. Open **AWS Lambda** in the AWS Console.
2. Select `everyday-calculator` -> click **Actions** -> **Delete function**.
3. Open **CloudWatch** -> **Log groups** -> select `/aws/lambda/everyday-calculator` -> **Actions** -> **Delete log group(s)**.
4. Open **IAM** -> **Roles** -> search for `everyday-calculator-role-...` (auto-generated during Lambda creation) -> click **Delete**.
Every associated resource is now completely removed with zero lingering charges.

---

## Known Limitations

1. **Number Guessing Game Session Persistence:**
   - Active guessing games are stored in JVM memory (`ConcurrentHashMap`). In serverless execution, state persists while the Lambda execution environment remains warm. If the environment scales to zero or rotates, an ongoing game session resets and prompts the player to click *Start a new game*. This matches the stateless design of modern web calculators.
2. **Cold Starts:**
   - The first request after a period of inactivity may experience a 3–6 second cold start while the JVM initializes the Spring Boot application context. Subsequent requests execute in 15–50 ms. Enabling AWS Lambda SnapStart eliminates cold-start latency.
