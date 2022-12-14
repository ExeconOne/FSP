Add `fsp-mongo` dependency to your project

```xml

<dependency>
    <groupId>pl.execon.commons</groupId>
    <artifactId>fsp-mongo</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

<details>
<summary><b>If you use @EnableMongoRepositories annotation</b></summary>

[`@EnableMongoRepositories`](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/repository/config/EnableMongoRepositories.html)
&larr; spring docs

Add `"pl.execon.fsp.*"` to `basePackages`

```diff
    @EnableMongoRepositories(basePackages = {
        "your.repository.path",
+        "pl.execon.fsp.*"
    })
```

</details>

---

In your repository interface extend `MongoFsp<T>` interface. This will enable on this repository method `findFsp()`.

```diff
+  extends MongoFsp<T>
```

```java

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, MongoFsp<Product> {
}
```

Last step is using `findFsp()` method in your service passing `FspRequest` and mongo DocumentClass.

```java
public FspResponse<Product> getFilteredProduct(){
        return repository.findFsp(new FspRequest(), Product.class);
        }
```

---
Example of use FSP for Product mongo document.

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

---

<details>
<summary><b>FspRequest mapping funcion</b></summary>

You can also call mapping function on FspResponse object to map your result to for example DTO object

```java
public class ProductService {
    public FspResponse<ProductDTO> getFilteredProducts(FspRequest fspRequest) {
        FspResponse<Product> products = repository.findFsp(fspRequest, Product.class);
        return products.map(product -> mappingFunction(product));
    }
}
```

</details>

---

### [Back to README](README.md)
