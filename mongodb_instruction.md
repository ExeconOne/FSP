
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

In your repository interface extend `MongoFsp<T>` interface in your repository interface. This will enable on this repository method `findFsp()`.
```java
@Repository
public interface ProductRepository extends MongoRepository<Product, String>,  MongoFsp<Product>{}
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
