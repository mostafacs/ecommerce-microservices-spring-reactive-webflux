
# Endpoints

## Authentication

### Register as client
```
curl -i -X POST \
   -H "Content-Type:application/json" \
   -d \
'{
  "email": "client@gmail.com",
  "firstName": "Mostafa",
  "lastName": "Albana",
  "password": "123"
}' \
 'http://localhost:8765/uaa/user/client'
```

### Register as Merchant

```
 curl -i -X POST \
    -H "Content-Type:application/json" \
    -d \
 '{
   "email": "merchant@gmail.com",
   "firstName": "Mostafa",
   "lastName": "Albana",
   "password": "123"
 }' \
  'http://localhost:8765/uaa/user/merchant'
```

### Login 
```
curl client%40gmail.com:123@localhost:8087/oauth/token -d grant_type=client_credentials

curl merchant%40gmail.com:123@localhost:8087/oauth/token -d grant_type=client_credentials
```

##<<< NOTE >>>
Please replace ${token} with active token you get after merchant login

## Product 

---- Create or update (Just specify id to update) (Only merchant role allowed)
```
curl -i -X POST \
   -H "Authorization:Bearer ${token}" \
   -H "Content-Type:application/json" \
   -d \
'{
 "sku":"ps4-pro-99",
 "title":"PlayStation 4 Pro",
 "costPrice":25.5,
 "sellPrice": 32.2,
 "inventoryCounts":2
}' \
 'http://localhost:8765/product/save'
```


--- List Products created by Merchant (Only merchant role allowed)
```
curl -i -X GET \
   -H "Authorization:Bearer ${token}" \
 'http://localhost:8765/product/list/merchant?page=0&pageSize=2'
```
 --- List products with quantity more than 0
 ```
curl -i -X GET \
   -H "Authorization:Bearer ${token}" \
 'http://localhost:8765/product/list/available?page=0&pageSize=2'
 ```

 --- List products with quantity equal to 0
 ```
curl -i -X GET \
   -H "Authorization:Bearer ${token}" \
 'http://localhost:8765/product/list/not-available?page=0&pageSize=2'
 ```
## Order
--- Create Order (Only Client Role Allowed)
```
curl -i -X POST \
   -H "Authorization:Bearer ${token}" \
   -H "Content-Type:application/json" \
   -d \
'{

  "cartItemList": [
    {"product": {"id": 4}, "quantity": 1} 
  ]
}' \
 'http://localhost:8765/order/save'
```
--- update order
```
curl -i -X PUT \
   -H "Authorization:Bearer ${token}" \
   -H "Content-Type:application/json" \
   -d \
'{

  "cartItemList": [
    "id": 26
    {"product": {"id": 4}, "quantity": 1} 
  ]
}' \
 'http://localhost:8765/order/save'
```

--- Get user orders (Only Client Role Allowed)
```
curl -i -X GET \
   -H "Authorization:Bearer ${token}" \
 'http://localhost:8765/order/list/user'
```

--- Get order by id (Only Client Role Allowed)
replace ${orderId} with your order id
```
curl -i -X GET \
   -H "Authorization:Bearer ${token}" \
 'http://localhost:8765/order/${orderId}'
```
