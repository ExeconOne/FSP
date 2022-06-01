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

In main application class add `@EnableMongoRepositories` annotation and in basePackages 
next to your repository path add `pl.execon.fsp.*` to enable FSP module component scan.

```
@EnableMongoRepositories(basePackages = {
        "path.to.your.project.*",
        "pl.execon.fsp.*"
})
```

Then extend interface `MongoFsp<T>` in your repository interface. This will enable on this repository method `findFsp()`.
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

public FspResponse<TodoItem> getFilteredProducts() {
    FspRequest fspRequest = FspRequest.builder()
            .filter(List.of(
                    new FilterInfo("name", Operation.EQUALS, "bike")
            )).build();

    return repository.findFsp(fspRequest, Product.class);
    }
}
```
