## Author
#### Name: Ikechukwu Michael, Email: mikeikechi3@gmail.com
## Introduction🖖
My solution to [minTYN](https://mintyn.com/) technical assessment.

This Card Verification Service is a Spring Boot microservice designed to provide customers with detailed information about their cards. The service aims to offer a seamless experience by verifying the validity, scheme (VISA, MASTERCARD, or AMEX), and associated bank for a given card number. Additionally, the service maintains statistics on the number of verification requests made by users.

### Specification

- Users are able to sign up using their email and password.
- Users can log in using a username and password.
- Upon successful profile creation, users are able to retrieve insights for their card.
- Users are also able to retrieve statistical insights for verifiable cards.

---

### Step One - Tools and Technologies used 🎼

- Spring Boot(V2.7.3)
- Spring Data JPA
- Lombok Library
- JDK 18
- Embedded Tomcat
- Mysql Database(Mysql Workbench)
- Maven
- Java IDE (IntelliJ)
- Postman Client

---

### Step Two - Steps to Run the project Locally ⚙️

[MySQL Workbench](https://www.mysql.com/products/workbench) was used to run the database locally. Navigate to the project application.yml file and modify the database credential per your database server requirement such as `username` and `password`
```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/cardInsight?createDatabaseIfNotExist=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
    username: #Database-username
    password: #Database-password
```
## Installation

* Clone this repo:
```bash
git clone https://github.com/michaelik/meCash-TC.git
```
* Navigate to the root directory of the project.
* Build the application
```bash
./mvnw clean install
```
* Run the application
```bash
./mvnw spring-boot:run
```
---

## Usage 🧨

>REST APIs For Card Insight Resource
> 
>Host: http://localhost:8080 

| HTTP METHOD |                    ROUTE                    | STATUS CODE |                 DESCRIPTION                 |
|:------------|:-------------------------------------------:|:------------|:-------------------------------------------:|
| POST        |            `/api/auth/register`             | 201         |               Create new user               |
| POST        |              `/api/auth/login`              | 200         |    Login into your newly created account    |
| GET         |   `/api/v1/card-scheme/user-detail/{id}`    | 200         |              Read user detail               |
| GET         |     `/api/v1/card-scheme/verify/{bin}`      | 200         |   Get issuer identification number detail   |
| GET         | `/api/v1/card-scheme/stats?{start}&{limit}` | 200         | Get issuer identification number statistics |

---

### The Client should be able to:

**SignUp**

The payload will have the following fields:

- `name`:
- `email`:
- `password`
- `age`:
- `gender`:
- `accountCurrency`:

Request

```
curl -X POST http://localhost:8080/api/auth/register \
-H 'Content-type: application/json' \
-d '{
    "name": "Ikechukwu Michael",
    "email": "mikeikechi3@gmail.com",
    "password": "12345",
    "age": 20,
    "gender": "M"
}'
```
**SignIn**

The payload will have the following field:
- `username`:
- `password`:

Request

```
curl -X POST http://localhost:8080/api/auth/login \
-H 'Content-type: application/json' \
-d '{
    "username": "mikeikechi3@gmail.com",
    "password": "12345"
}'
```

Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6Im1pa2Vpa2VjaGkzQGdtYWlsLmNvbSIsImlhdCI6MTY5MjIwOTQ2MiwiZXhwIjoxNjkzNTA1NDYyfQ.tWbASUsAzxtOjVCGWB9_dNn6qmm35IATzoNT9QQmmUY",
  "id": 1,
  "name": "michael ikechukwu",
  "email": "mikeikechi3@gmail.com",
  "gender": "M",
  "age": 20,
  "roles": [
    "ROLE_USER"
  ],
  "username": "mikeikechi3@gmail.com"
}
```
**Read Account Detail**

Request

```
curl -X GET http://localhost:8080/api/v1/card-scheme/user-detail/1 \
-H 'Content-type: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6Im1pa2Vpa2VjaGkzQGdtYWlsLmNvbSIsImlhdCI6MTY5MjIwOTQ2MiwiZXhwIjoxNjkzNTA1NDYyfQ.tWbASUsAzxtOjVCGWB9_dNn6qmm35IATzoNT9QQmmUY' \
```

Response

```json
{
  "id": 1,
  "name": "michael ikechukwu",
  "email": "mikeikechi3@gmail.com",
  "age": 20,
  "gender": "M"
}
```
**Verify Bank Identification Number**

Request

```
curl -X GET http://localhost:8080/api/v1/card-scheme/verify/53992370
-H 'Content-type: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6Im1pa2Vpa2VjaGkzQGdtYWlsLmNvbSIsImlhdCI6MTY5MjIwOTQ2MiwiZXhwIjoxNjkzNTA1NDYyfQ.tWbASUsAzxtOjVCGWB9_dNn6qmm35IATzoNT9QQmmUY' \
```

Response

```json
{
  "success": true,
  "payload": {
    "scheme": "mastercard",
    "type": "debit",
    "bank": "FIRST"
  }
}
```
**Read Card Statistics**

Request

```
curl -X GET http://localhost:8080/api/v1/card-scheme/stats?1&2
-H 'Content-type: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6Im1pa2Vpa2VjaGkzQGdtYWlsLmNvbSIsImlhdCI6MTY5MjIwOTQ2MiwiZXhwIjoxNjkzNTA1NDYyfQ.tWbASUsAzxtOjVCGWB9_dNn6qmm35IATzoNT9QQmmUY' \
```

Response

```json
{
  "success": true,
  "start": 1,
  "limit": 2,
  "size": 1,
  "payload": {
    "53992370": "1"
  }
}
```