package pl.execon.fsp.mongo;

import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

public interface MongoFsp<T> {

    FspResponse<T> findFsp(FspRequest request, Class<T> documentClass);
}
