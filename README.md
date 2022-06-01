# Filter-Sort-Page (FSP)

---
## Description
FSP is a library that helps you filter and sort objects in your database using the easy-to-build FspRequest

---
## How to Use
This section describes how to use FSP with different databases.

### For MongoDB

Add `fsp-mongo` dependency to your project

```xml
<dependency>
    <groupId>pl.execon.common</groupId>
    <artifactId>fsp-mongo</artifactId>
    <version>0.1.0</version>
</dependency>
```

---
**If you use `@EnableMongoRepositories` annotation**

Add `"pl.execon.fsp.*"` to `basePackages`

---

Extend interface `MongoFsp<T>` in your repository interface. This will enable on this repository method `findFsp()`.
```java
@Repository
public interface ProductRepository extends MongoRepository<Product, String>, MongoFsp<Product>{}
```

Last step is using `findFsp()` method in your service passing `FspRequest` and mongo DocumentClass.
```
public FspResponse<Product> getFilteredProduct() {
    return repository.findFsp(new FspRequest(), Product.class);
}
```

Example of FspRequest for Product mongo document.
```java
@Document
public class Product {
    
    @Id
    private String id;
    
    private String name;
    private int quantity;
}    
```

```java
@Service
@AllArgsConstructor
public class ProductService {

private final ProductRepository repository;

public FspResponse<Product> getFilteredProducts(FspRequest fspRequest) {
    return repository.findFsp(fspRequest, Product.class);
    }
}
```
---
You can also call mapping function on FspResponse object to map your result to for example DTO object

```java
public class ProductService {
    public FspResponse<ProductDTO> getFilteredProducts(FspRequest fspRequest) {
        FspResponse<Product> products = repository.findFsp(fspRequest, Product.class);
        return products.map(product -> mappingFunction(product));
    }
}
```
---

```java
@RestController
@AllArgsConstructor
public class ProductController {

private final ProductService service;

    @PostMapping("/fsp")
    public FspResponse<Product> getFilteredProducts(@RequestBody FspRequest fspRequest) {
        return service.getFilteredProducts(fspRequest);
    }
}
```
Example FspRequest body
```json
{
  "filter": [
    {
      "by": "name",
      "operation": "NOT_EQUALS",
      "value": "bike",
      "operator": "AND"
    }
  ],
  "sort": [
    {
      "by": "name",
      "direction": "ASC"
    }
  ],
  "page": {
    "number": 0,
    "size": 10
  }
}
```

## License
FSP is Open Source software released under the
https://www.apache.org/licenses/LICENSE-2.0.html [Apache 2.0 license].

---
## Changelog

### Ver. 0.1.0

+ Added fsp-model module
+ Added fsp-mongo which contains fsp support for mongodb
