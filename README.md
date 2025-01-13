<h1><b>Fly3 on air shopping</b></h1>

Products of different categories can be created for purchase.
A purchase ordering system is implemented in which an order lifecycle goes as follows: 
- orders are open with the seat identification
- orders are updated with customer's email and the list of selected items
- orders are finished with a card payment 

-- DB storage
H2 in memory database used for persisting the data. For production it should be changed to a real DB.
Use this params in the applications.properties file:
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:dcbapp
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

-- Logging
Logging has been defined in the applications.properties file: 
logging.file.name=logs/app.log
logging.level.root=INFO

--------------

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://https://github.com/jl-migtron/fly3.git
```

**2. Run the app using maven**

```bash
mvnw spring-boot:run
```
The app will start running at <http://localhost:8080>

## Rest APIs

The app defines following CRUD APIs.

### Categories

| Method | Url | Description | Params and Request Body |
| ------ | --- | ----------- |
| GET    | /api/categories | Get all categories | 
| GET    | /api/categories/{id} | Get category by id | 
| POST   | /api/categories | Create new category | [JSON](#category) |
| PUT    | /api/categories/{id} | Update category | [JSON](#category) | 
| DELETE | /api/categories/{id} | Delete category |

### Products

| Method | Url | Description | Params and Request Body |
| ------ | --- | ----------- |
| GET    | /api/categories/{catId}/products | Get all products for category | 
| GET    | /api/products/{id} | Get product by id | 
| POST   | /api/categories/{catId}/products | Create new product for category | [JSON](#product) |
| PUT    | /api/products/{id} | Update product | [JSON](#product) |
| DELETE | /api/products/{id} | Delete product |


### Orders

| Method | Url | Description | Params and Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/orders | Get all orders |
| GET    | /api/orders/{id} | Get order by id |
| GET    | /api/orders/status | Get orders by status | *Request params:* status (OPEN, DROPPED, FINISHED) 
| POST   | /api/orders | Create new order | *Request params:* seatnum, seatletter | 
| PUT    | /api/orders/{id}/update | Update order with selected products | *Request params:* email, [JSON](#items) | 
| PUT    | /api/orders/{id}/finish | Finish order with payment | [JSON](#payment) | 
| DELETE | /api/orders/{id} | Delete order | |


Test them using postman or any other rest client.

## JSON Request Bodies


##### <a id="category">Create category -> /api/categories</a>
```json
{"id":null,"name":"drinks","parentCat":null,"products":null}
```

##### <a id="product">Create product -> /api/categories/{catId}/products</a>
```json
{"id":null,"name":"coke","price":100,"category":null,"image":"C:\\coke.img"}
```

##### <a id="items">Update order -> /api/orders/{id}/update</a>
```json
[{"id":null,"productId":1,"quantity":4,"price":100,"order":null},{"id":null,"productId":2,"quantity":4,"price":100,"order":null}]
```

##### <a id="payment">Finish order -> /api/orders/{id}/finish</a>
```json
{"card":"K998877","payStatus":"PAID","date":1736553763658,"gateway":"VISA"}
```


## STOCK SIMULATION: 
A SimpleStock component has been provided for tests purposes. It's composed by 20 products (ids 1L to 20L), all with 10 units available. 
For production a proper repository, a service and a rest controller should be created for real stock management. 

## STOCK SAFETY:
To avoid stock race conditions (two customers ordering the same product) a product stock is considered "consumed" as soon as orders are updated (still open).
If the order is eventually cancelled or finished with failed payment the "consumed" stocks are automatically "restored". 

## Test examples:

-- add categories
POST localhost:8080/api/categories {"id":null,"name":"drinks","parentCat":null,"products":null}
POST localhost:8080/api/categories {"id":null,"name":"food","parentCat":null,"products":null}
GET localhost:8080/api/categories

-- add products for drinks category
POST localhost:8080/api/categories/1/products  {"id":null,"name":"coke","price":100,"category":null,"image":"C:\\coke.img"}
POST localhost:8080/api/categories/1/products  {"id":null,"name":"fanta","price":100,"category":null,"image":"C:\\fanta.img"}
GET localhost:8080/api/categories
GET localhost:8080/api/categories/1/products

-- change product price
PUT localhost:8080/api/products/1 {"id":null,"name":"coke","price":120,"image":"C:\\coke.img"}
GET localhost:8080/api/categories/1/products

-- add orders
POST localhost:8080/api/orders?seatnum=25&seatletter=B
POST localhost:8080/api/orders?seatnum=28&seatletter=A
POST localhost:8080/api/orders?seatnum=29&seatletter=D
POST localhost:8080/api/orders?seatnum=31&seatletter=C
GET localhost:8080/api/orders/status?status=OPEN

-- update orders
PUT localhost:8080/api/orders/1/update?email=mark@gmail.com  [{"id":null,"productId":1,"quantity":4,"price":100,"order":null},{"id":null,"productId":2,"quantity":4,"price":100,"order":null}]
PUT localhost:8080/api/orders/2/update?email=tony@gmail.com  [{"id":null,"productId":1,"quantity":8,"price":100,"order":null},{"id":null,"productId":2,"quantity":8,"price":100,"order":null}]
PUT localhost:8080/api/orders/3/update?email=betty@gmail.com  [{"id":null,"productId":4,"quantity":8,"price":100,"order":null},{"id":null,"productId":5,"quantity":8,"price":100,"order":null}]
GET localhost:8080/api/orders
(important: in order 2 the service has limited the ordered quantities from 8 to 6 due to exhausted stocks (10 units available), the resulting price is coherent with that, ie 1200)

-- finish order with payment OK
PUT localhost:8080/api/orders/1/finish {"card":"K998877","payStatus":"PAID","date":1736553763658,"gateway":"VISA"}
GET localhost:8080/api/orders/status?status=FINISHED

-- finish order with payment FAILED
PUT localhost:8080/api/orders/2/finish {"card":"J112266","payStatus":"PAYMENTFAILED","date":1736553763658,"gateway":"VISA"}
GET localhost:8080/api/orders/status?status=DROPPED

-- finish order with payment FAILEDOFFLINE
PUT localhost:8080/api/orders/3/finish {"card":"L0003446","payStatus":"OFFLINEPAYMENT","date":1736553763658,"gateway":"VISA"}
GET localhost:8080/api/orders/status?status=FINISHED

-- cancel order 
PUT localhost:8080/api/orders/4/cancel
GET localhost:8080/api/orders/status?status=DROPPED