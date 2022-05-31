package pl.execon.fsp.mongo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

import java.util.List;

@RequiredArgsConstructor
public class MongoFspImpl<T> implements MongoFsp<T> {

    @NonNull
    private MongoTemplate mongoTemplate;

    @Override
    public FspResponse<T> findFsp(FspRequest request, Class<T> documentClass) {
        Query query = new MongoFspRequestResolver(request).asQuery();
        List<T> list = mongoTemplate.find(query, documentClass);

        if (!request.hasPageInfo()) {
            return new FspResponse<>(list);
        }

        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), documentClass);
        return new FspResponse<>(request, list, count);
    }
}
