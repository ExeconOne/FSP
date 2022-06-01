package pl.execon.fsp.mongo;

import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

/**
 * This is an interface which delivers FSP functionality.
 *
 * @param <T> type of consumed object
 */
public interface MongoFsp<T> {

    /**
     * Method which allows filtering, paging and sorting for given mongo document class.
     *
     * @param request request with filter, paging and sorting
     * @param documentClass mongo @Document class
     * @return result of given request for given documentClass
     */
    FspResponse<T> findFsp(FspRequest request, Class<T> documentClass);
}
