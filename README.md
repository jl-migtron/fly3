<h1><b>Fly3 on air shopping</b></h1>

<i>Products and categories</i> can be created for purchase.
A purchase ordering system is implemented in which an order lifecycle goes as follows: 
- orders are open with the seat identification
- orders are updated with customer's email and the list of selected items
- orders are finished with a card payment 

--------------

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://https://github.com/jl-migtron/fly3.git
```

**2. Run the app using maven**

```bash
mvn spring-boot:run
```
The app will start running at <http://localhost:8080>

## Explore Rest APIs

The app defines following CRUD APIs.

### Categories

| Method | Url | Description |
| ------ | --- | ----------- |
| GET    | /api/categories | Get all categories | 
| GET    | /api/categories/{id} | Get category by id | 
| POST   | /api/categories | Create new category | [JSON](#category) |
| PUT    | /api/categories/{id} | Update category | [JSON](#category) | 
| DELETE | /api/categories/{id} | Delete category |

### Products

| Method | Url | Description |
| ------ | --- | ----------- |
| GET    | /api/categories/{catId}/products | Get all products for category | 
| GET    | /api/categories/products/{id} | Get product by id | 
| POST   | /api/categories/{catId}/products | Create new product for category | [JSON](#product) |
| PUT    | /api/categories/products/{id} | Update product | [JSON](#product) |
| DELETE | /api/categories/products/{id} | Delete product |


### Orders

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/orders | Get all orders |
| GET    | /api/orders/{id} | Get order by id |
| GET    | /api/orders/status | Get orders by status | *Request params:* OrderStatus |
| POST   | /api/orders | Create new order | *Request params:* seatnum, seatletter |
| PUT    | /api/orders/{id}/update | Update order with selected products | *Request params:* email, [JSON](#items) |
| PUT    | /api/orders/{id}/finish | Finish order with payment | [JSON](#payment) |
| DELETE | /api/orders/{id} | Delete order | |


Test them using postman or any other rest client.

## Sample Valid JSON Request Bodys


##### <a id="category">Create category -> /api/categories</a>
```json
{"id":25,"name":"drinks","parentCat":null,"products":null}
```

##### <a id="product">Create product -> /api/categories/products</a>
```json
{"id":123,"name":"coke","price":100,"category":{"id":25,"name":"drinks","parentCat":null,"products":null},"image":"C:\\coke.img"}
```

##### <a id="payment">Finish order -> /api/orders/{id}/finish</a>
```json
{"card":"K998877","payStatus":"PAID","date":1736553763658,"gateway":"VISA"}
```

##### <a id="items">Update order -> /api/orders/{id}/update</a>
```json
[{"id":null,"productId":123,"quantity":4,"price":100,"order":null},{"id":null,"productId":124,"quantity":4,"price":100,"order":null}]
```

