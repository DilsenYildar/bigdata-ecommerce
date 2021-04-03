# bd-ecommerce

## pre-requests

docker run -d --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=root mongo

docker run --publish=7474:7474 --name=neo4j -d --publish=7687:7687 -e 'NEO4J_AUTH=neo4j/secret' neo4j:4.0.0

redis and postgres..


###
###
#### CUSTOMER API's...
###
POST http://localhost:8090/customer
Content-Type: application/json

{
  "username": "testuser",
  "password": 1234,
  "email" : "testuser@gmail.com"
}

###
GET http://localhost:8090/customer/testuser

###
DELETE http://localhost:8090/customer/testuser



#### PRODUCT API's...
###
POST http://localhost:8090/product
Content-Type: application/json

{
  "name": "testurunX",
  "price": 50,
  "brand": "Y markası",
  "quantity": "5"
}


###
PUT http://localhost:8090/product/testurunX
Content-Type: application/json

{
  "price": 30,
  "brand": "Y markası",
  "quantity": "10"
}

###
GET http://localhost:8090/product/5656


###
DELETE http://localhost:8090/product/4545




#### SESSION API's....
###
POST http://localhost:8090/session
Content-Type: application/json

{
  "username": "testuser2",
  "password": 1234
}

###
DELETE http://localhost:8090/session/testuser




#### SHOPPING-CART API's....
###
POST http://localhost:8090/shopping-cart/testuser2/testurunY
Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=


###
GET http://localhost:8090/shopping-cart/testuser
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=

###
DELETE http://localhost:8090/shopping-cart/testuser/4545
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=




#### ORDER API's....
### create order
POST http://localhost:8090/order
Content-Type: application/json
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=

{"username" : "testuser2"}

###  address güncellenebilir olmalı.
PUT http://localhost:8090/order/6030fce81fc5da392e4faca8
Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=

{"username" : "testuser", "items" : [{"name" : "4545", "price" : "23424"}]}

###
GET http://localhost:8090/order
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=

###
DELETE http://localhost:8090/order/6030ffd61fc5da392e4facbe
Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=

###
DELETE http://localhost:8090/order/
Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
Authorization: Basic dGVzdHVzZXIyOjEyMzQ=





#### PRODUCT-BUYERS API's....
###  search for users who bought product
POST http://localhost:8090/product-buyers/users
Content-Type: application/json
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2

{"productName" :  "testurunY"}


### create product node.
#POST http://localhost:8090/product-buyers
#Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
##Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2
#
#{"name":  "4545", "brand" :  "X markası", "price": 10}

#### create user node.
#POST http://localhost:8090/product-buyers/user
#Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
##Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2
#
#{"username" : "test user"}

#### create relation between product and user.
#POST http://localhost:8090/product-buyers/user/assign
#Content-Type: application/json
#Authorization: Basic dGVzdHVzZXI6MTIzNA==
##Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2
#
#{"username" : "test user", "productName" :  "4545"}

### gets products
GET http://localhost:8090/product-buyers/
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2

### get product by id
GET http://localhost:8090/product-buyers/2
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2

### delete product
DELETE http://localhost:8090/product-buyers/0
Authorization: Basic dGVzdHVzZXI6MTIzNA==
#Authorization: Basic dGVzdHVzZXIyOjEyMzQ=  ## testuser2
