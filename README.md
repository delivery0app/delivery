# Delivery app

This is the backend part of a delivery application, developed in Java and Spring Framework. Our project offers functionality for three roles: Couriers, Customers, and an Admin who can manage the entire process, including orders, couriers, and customers. The application is designed to simplify and optimize delivery processes.

## Stack
- Spring Boot v.3.1.4
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- FlyWay
- Swagger
- Junit + Mockito
- Docker

## Includes 7 Java directories
- config - security configuration files;
- controllers - 5 controller classes;
- dto - a set of objects for data transfer;
- models - models of User, Role, Courier, Customer, and Order objects;
- repositories - repositories for models;
- services - business logic;
- util - classes for exception handling, validation, order cost calculation, and more;
- tests - unit and integration testing.
  
## Deployment
There are several ways to run a Spring Boot application on your local machine. One way is to execute the docker-compose.yml file from your IDE.

```bash
  docker-compose up
```
Another way:
1. Build the project using `mvn clean install`
2. Run using `mvn spring-boot:run`
3. The web application is accessible via localhost:8080
4. Use username and password as 'admin' to login to demo (see below).

## About the application
Application for managing delivery orders of cargo from point A to point B. It uses an relational database (PostgreSQL) to store the data. If your database connection properties work, you can call some REST endpoints defined in ```com/factglobal/delivery/controllers``` on **port 8080**. (see below) However, first, you need to authenticate as an Admin, Courier or Customer. 

Example:
###  Authenticate as an Admin

```
POST /auth
Content-Type: application/json

{
"phoneNumber" : "+79999999987",
"password" : "SecureP@ss2"
}

RESPONSE: HTTP 200 (OK)
BODY: "JWT token"
```

Here are some endpoints you can call:

### Get information about all orders, couriers, customers for the Admin.

```
http://localhost:8080/orders
http://localhost:8080/admins/couriers
http://localhost:8080/admins/customers
```

### Edit an existing Order for the Admin

```
PUT /orders/1/admin
Content-Type: application/json

{
  "senderAddress": "Moscow",
  "deliveryAddress": "Paris",
  "weight": "5",
  "description": "Cargo description",
  "paymentMethod": "CARD",
  "fragileCargo": "TRUE"
}

RESPONSE: HTTP 200 (OK)
```
### Assign a courier to an order for the Admin

```
PUT orders/1/couriers/1/assign

RESPONSE: HTTP 200 (OK)
```

### To view Swagger API docs

Run the server and browse to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Authors
- [Pavel Kastsiuchyk](https://github.com/kostuchik)
- [Raman Shumko](https://github.com/ramanshumko)
