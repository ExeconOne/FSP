Add `fsp-relational` dependency to your project

```xml

<dependency>
    <groupId>pl.execon.common</groupId>
    <artifactId>fsp-relational</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

In your repository interface extend `RelationalFsp<T>` interface in your repository interface. This will enable on this
repository method `findFsp()`.

```diff
+  extends RelationalFsp<T>
```

```java

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, RelationalFsp<Product> {
}
```

Last step is using `findFsp()` method in your service passing `FspRequest`.

```java
public FspResponse<Product> getFilteredProduct(){
        return repository.findFsp(new FspRequest());
        }
```

---
Example of use FSP for Product entity.

```java

@Entity
public class Product {

    @Id
    private Long id;

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
        return repository.findFsp(fspRequest);
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
