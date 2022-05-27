package pl.execon.fsp.mongo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MongoFspImpl<T> implements MongoFspRepository<T> {

    @NonNull
    private MongoTemplate mongoTemplate;

    @Override
    public FspResponse<T> findFsp(FspRequest request) {
        Query query = new MongoFspRequestResolver(request).asQuery();
        List<T> list = mongoTemplate.find(query, getClazzType());

        if (!request.hasPageInfo()) {
            return new FspResponse<>(list);
        }

        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), this.getClass().getGenericSuperclass().getClass());
        return new FspResponse<>(request, list, count);
    }

    @Override
    public Class<T> getClazzType() {
        return null;
    }
}
