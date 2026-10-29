# Everyday Calculators

A browser-based version of the original Java calculator. The Spring Boot application serves a responsive frontend and JSON REST API from the same process and container. Calculation requests do not require a terminal or store personal data.

## Features

- **BMI:** kilograms/meters formula and the original Underweight, Normal weight, Overweight, and Obesity cutoffs.
- **Math:** addition, subtraction, multiplication, division, exponentiation, square root, factorial, modulus, sine, cosine, tangent, base-10 logarithm, natural logarithm, and absolute value. Trigonometry uses degrees.
- **Number guessing game:** random integer from 1 to 100, with higher/lower hints and an attempt count.
- **Unit conversion:** length, weight, temperature, volume, and area conversions using the original conversion factors.
- **Area:** circle, rectangle, triangle, and square.
- **Volume:** sphere, cube, cylinder, cuboid, and cone.
- **Validation:** clear client-side constraints and JSON errors from the API.

The original [Project.java](./Project.java) console source and its existing generated `Project.class` are retained as references. The class file is ignored by Git and Docker and is not used by the Maven build, which compiles the Spring Boot application from `src/main/java`.

## Architecture

```text
Browser (HTML/CSS/vanilla JavaScript)
  ├── GET /                 Spring Boot static resource
  └── POST /api/...         REST controllers → calculator services
                               └── in-memory guessing-game state
```

The service classes keep the formulas separate from HTTP handling. Guessing games are held in process memory; a restart or a different ECS task loses that game. The app does not persist requests or user data.

## Technology

- Java 25 (LTS)
- Spring Boot 3.5.3, Spring MVC, Jakarta Bean Validation
- Maven
- HTML, CSS, vanilla JavaScript
- Docker multi-stage build; Amazon ECR and ECS Fargate deployment template

## Prerequisites

- JDK 25 (LTS)
- Maven 3.6.3 or later
- Docker Desktop (for container build/run)

No AWS account is needed for local development.

## Run locally

From the repository root:

```powershell
mvn spring-boot:run
```

Or build and launch the packaged application:

```powershell
mvn -B verify
java -jar target/java-project-1.0.0.jar
```

The server binds to `0.0.0.0` and defaults to port `8080`. Override the port with `PORT`, for example in PowerShell:

```powershell
$env:PORT = "9090"
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) (or the configured port). The health endpoint is [http://localhost:8080/api/health](http://localhost:8080/api/health).

## Build and test

```powershell
mvn -B test
mvn -B verify
```

`verify` runs the service unit tests and Spring MVC API tests and packages the executable JAR.

## Docker

Build and run locally:

```powershell
docker build -t everyday-calculators .
docker run --rm -p 8080:8080 everyday-calculators
```

Open [http://localhost:8080](http://localhost:8080). Check container health/API:

```powershell
docker inspect --format='{{.State.Health.Status}}' (docker ps -q --filter ancestor=everyday-calculators)
curl.exe http://localhost:8080/api/health
```

The final image contains only the executable JAR and Java 25 JRE, runs as a non-root user, and includes a health check for `/api/health`. To use another port, set the same container port mapping and environment value, for example `docker run --rm -e PORT=9090 -p 9090:9090 everyday-calculators`.

## REST API

All endpoints are served from the same origin as the website. Successful responses use JSON; invalid requests return HTTP 400 with `{"error":"..."}`.

| Method and path | Request | Result |
|---|---|---|
| `GET /api/health` | — | `{"status":"UP"}` |
| `POST /api/bmi` | `{"weightKg":70,"heightMeters":1.75}` | `{"bmi":22.857142857142858,"category":"Normal weight"}` |
| `POST /api/math` | `{"operation":"ADD","firstValue":2,"secondValue":3}` | `{"operation":"ADD","result":5.0}` |
| `POST /api/games/guess/start` | `{}` | `{"gameId":"<id>","message":"A number between 1 and 100 has been chosen."}` |
| `POST /api/games/guess/{gameId}` | `{"guess":50}` | `{"message":"Too low! Try again.","attempts":1,"correct":false}` |
| `POST /api/conversions` | `{"category":"LENGTH","conversion":"METERS_TO_FEET","value":1}` | `{"category":"LENGTH","conversion":"METERS_TO_FEET","input":1.0,"result":3.28084}` |
| `POST /api/geometry/area` | `{"shape":"CIRCLE","firstDimension":2}` | `{"shape":"CIRCLE","result":12.566370614359172}` |
| `POST /api/geometry/volume` | `{"shape":"CUBOID","firstDimension":2,"secondDimension":3,"thirdDimension":4}` | `{"shape":"CUBOID","result":24.0}` |

Math operations are `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`, `POWER`, `SQRT`, `FACTORIAL`, `MODULUS`, `SIN`, `COS`, `TAN`, `LOG10`, `LN`, and `ABS`. Unary operations omit `secondValue`. Factorial accepts non-negative integers up to 170 to prevent non-finite results.

Conversion categories are `LENGTH`, `WEIGHT`, `TEMPERATURE`, `VOLUME`, and `AREA`. The conversion enum values are `METERS_TO_FEET`, `FEET_TO_METERS`, `KILOMETERS_TO_MILES`, `MILES_TO_KILOMETERS`, `KILOGRAMS_TO_POUNDS`, `POUNDS_TO_KILOGRAMS`, `GRAMS_TO_OUNCES`, `OUNCES_TO_GRAMS`, `CELSIUS_TO_FAHRENHEIT`, `FAHRENHEIT_TO_CELSIUS`, `CELSIUS_TO_KELVIN`, `KELVIN_TO_CELSIUS`, `LITERS_TO_GALLONS`, `GALLONS_TO_LITERS`, `MILLILITERS_TO_FLUID_OUNCES`, `FLUID_OUNCES_TO_MILLILITERS`, `SQUARE_METERS_TO_SQUARE_FEET`, `SQUARE_FEET_TO_SQUARE_METERS`, `HECTARES_TO_ACRES`, and `ACRES_TO_HECTARES`. Physical conversions require positive input; temperature accepts any finite numeric input, as in the original calculator.

Area shapes: `CIRCLE` and `SQUARE` use `firstDimension`; `RECTANGLE` and `TRIANGLE` also require `secondDimension`. Volume shapes: `SPHERE` and `CUBE` use `firstDimension`; `CYLINDER` and `CONE` also require `secondDimension`; `CUBOID` requires all three dimensions. Dimensions must be positive finite numbers.

Example:

```powershell
curl.exe -X POST http://localhost:8080/api/math `
  -H "Content-Type: application/json" `
  -d '{\"operation\":\"ADD\",\"firstValue\":2,\"secondValue\":3}'
```

## AWS deployment preparation

The deployment workflow is in [`.github/workflows/aws.yml`](./.github/workflows/aws.yml); the Fargate task template is [`.aws/task-definition.json`](./.aws/task-definition.json). A push to `main` runs Maven tests, builds the Docker image, pushes it to ECR, and updates the existing ECS service. It does **not** create AWS resources or a load balancer.

### Required GitHub configuration

Create a GitHub Actions environment named `production`. Add these **repository/environment variables** (not secrets):

| Variable | Value you supply |
|---|---|
| `AWS_REGION` | AWS region where you manually create the resources |
| `AWS_ROLE_ARN` | ARN of the IAM deployment role trusted for this repository’s OIDC identity |
| `ECR_REPOSITORY` | Name of your ECR repository |
| `ECS_CLUSTER` | Name of your ECS cluster |
| `ECS_SERVICE` | Name of the existing ECS service |
| `ECS_TASK_EXECUTION_ROLE_ARN` | ARN of the task execution role used by Fargate to pull the image and write logs |

The workflow fixes the task-definition path (`.aws/task-definition.json`) and container name (`calculator`). No account ID, cluster, service, repository, region, or role name is invented. Missing variables stop the workflow before AWS authentication. Restrict the `production` environment to deployments from the `main` branch, and consider requiring approval. Do not configure long-lived access-key secrets.

### OIDC trust and IAM

Create an IAM OIDC provider for `https://token.actions.githubusercontent.com` (audience `sts.amazonaws.com`) if the account does not already have it. The deployment role trust policy must restrict both audience and subject to this repository’s `production` environment:

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Federated": "arn:aws:iam::<YOUR_ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
    },
    "Action": "sts:AssumeRoleWithWebIdentity",
    "Condition": {
      "StringEquals": {
        "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
        "token.actions.githubusercontent.com:sub": "repo:AadarshChaudhary1/java-project:environment:production"
      }
    }
  }]
}
```

Grant the deployment role only the actions needed for this workflow:

- ECR authentication (`ecr:GetAuthorizationToken`, resource `*`) and image push actions (`ecr:BatchCheckLayerAvailability`, `ecr:CompleteLayerUpload`, `ecr:InitiateLayerUpload`, `ecr:PutImage`, `ecr:UploadLayerPart`) scoped to the one repository.
- ECS `ecs:RegisterTaskDefinition` and `ecs:DescribeTaskDefinition`, plus `ecs:DescribeServices` and `ecs:UpdateService` scoped to the relevant service/cluster as supported by each action.
- `iam:PassRole` only for `ECS_TASK_EXECUTION_ROLE_ARN`, conditioned on `iam:PassedToService` being `ecs-tasks.amazonaws.com`.

The following is a deployment-role **permissions policy** example matching the workflow. Replace every `<...>` placeholder with your own account, region, and configured resource values before attaching it; do not paste it unchanged. Keep this deployment role separate from the ECS task execution role.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AuthenticateToEcr",
      "Effect": "Allow",
      "Action": "ecr:GetAuthorizationToken",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "aws:RequestedRegion": "<AWS_REGION>"
        }
      }
    },
    {
      "Sid": "PushOnlyToCalculatorRepository",
      "Effect": "Allow",
      "Action": [
        "ecr:BatchCheckLayerAvailability",
        "ecr:CompleteLayerUpload",
        "ecr:InitiateLayerUpload",
        "ecr:PutImage",
        "ecr:UploadLayerPart"
      ],
      "Resource": "arn:aws:ecr:<AWS_REGION>:<AWS_ACCOUNT_ID>:repository/<ECR_REPOSITORY>"
    },
    {
      "Sid": "RegisterTaskDefinition",
      "Effect": "Allow",
      "Action": "ecs:RegisterTaskDefinition",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "aws:RequestedRegion": "<AWS_REGION>"
        }
      }
    },
    {
      "Sid": "DescribeCalculatorTaskDefinitions",
      "Effect": "Allow",
      "Action": "ecs:DescribeTaskDefinition",
      "Resource": "arn:aws:ecs:<AWS_REGION>:<AWS_ACCOUNT_ID>:task-definition/java-project:*"
    },
    {
      "Sid": "DeployOnlyToCalculatorService",
      "Effect": "Allow",
      "Action": [
        "ecs:DescribeServices",
        "ecs:UpdateService"
      ],
      "Resource": "arn:aws:ecs:<AWS_REGION>:<AWS_ACCOUNT_ID>:service/<ECS_CLUSTER>/<ECS_SERVICE>"
    },
    {
      "Sid": "PassOnlyTaskExecutionRoleToEcs",
      "Effect": "Allow",
      "Action": "iam:PassRole",
      "Resource": "<ECS_TASK_EXECUTION_ROLE_ARN>",
      "Condition": {
        "StringEquals": {
          "iam:PassedToService": "ecs-tasks.amazonaws.com"
        }
      }
    }
  ]
}
```

The separate ECS task execution role needs the `ecs-tasks.amazonaws.com` trust relationship and ECR image-pull/CloudWatch Logs write permissions (the AWS-managed `AmazonECSTaskExecutionRolePolicy` is a convenient baseline). Do not give the app a task role unless it later needs AWS API access.

### Resources to create manually

Before the first deployment, create/configure these in your account; the workflow deliberately does not provision them:

1. An ECR repository.
2. The GitHub OIDC provider and least-privilege deployment IAM role, and the ECS task execution role.
3. An ECS cluster, Fargate task/service, VPC, public subnet(s), and security group. Configure the service to assign a public IP if it should be reachable without a load balancer; allow inbound TCP 8080 only from the audience that needs access.
4. A CloudWatch log group named `/ecs/java-project` with a short retention period (for example, 7 days). The task definition writes logs there.

There is **no load balancer or TLS endpoint** in this template. With no load balancer, the task's public IP can change after redeployment/replacement and direct HTTP access is not suitable for sensitive data. A stable hostname and HTTPS require additional infrastructure, commonly a load balancer and certificate, and can add charges. Do not add these unless you choose to accept their cost.

### Free-plan and cost notes

You reported having a Free plan and $120 in credits expiring April 28, 2027. I cannot inspect your AWS account or verify the offer's eligibility, expiration, or remaining balance. AWS account offers vary and credits have service/region/expiration conditions; a Free plan or remaining credits do not guarantee that each resource below is free. Confirm the current balance and expiry in your AWS Billing console, review regional pricing, and set cost alerts before creating resources. The GitHub workflow does not contact AWS until you configure OIDC and push to `main`.

Potential metered usage to check against your credits and current regional pricing:

- **Fargate:** vCPU and memory are billed while tasks run; a continuously running service accrues usage. ECS control-plane use does not make the compute free.
- **Public IPv4:** a public IP assigned to a running task can be charged by the hour.
- **Load balancer:** not created by this project; ALB hours and capacity units may add ongoing charges if you add one.
- **ECR:** image storage is metered; keep only needed tags and remove old images.
- **CloudWatch Logs:** ingestion and retained storage may be charged. Configure short log retention.
- **Networking:** outbound data transfer may be charged; NAT Gateways can be a significant additional cost and are not required by the suggested public-subnet setup.
- **GitHub Actions:** usage may count against GitHub's plan limits; AWS usage during deployment is separate.

Use the AWS pricing pages and Billing/Cost Explorer for your region and account, and verify costs before proceeding. Credits can be consumed by these services.

### Cleanup

To stop ongoing AWS charges when finished, manually scale the ECS service to zero or delete it, remove idle task/network resources and any load balancer or NAT Gateway you added, delete the ECR images/repository when no longer needed, and delete or retain the CloudWatch log group intentionally. Review billing after cleanup; deleting a service alone does not remove every related resource.

## Git hygiene

`.gitignore` excludes compiled classes, Maven output, IDE settings, and local environment files. `.dockerignore` keeps Git metadata, credentials/environment files, IDE files, generated classes, and documentation out of the Docker build context. The original source remains; only its committed generated `.class` artifact is removed.
