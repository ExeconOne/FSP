package pl.execon.fsp.mongo;

import pl.execon.fsp.core.FspRepository;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

public interface MongoFspRepository<T> extends FspRepository<T> {

    Class<T> getClazzType();
}
