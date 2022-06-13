package pl.execon.fsp.oracle;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

import java.util.List;

/**
 * This is an interface which delivers FSP functionality for relational databases.
 *
 * @param <T> type of consumed object
 */
public interface OracleFsp<T> extends JpaSpecificationExecutor<T> {

    /**
     * Method which allows filtering, paging and sorting for given T param entity.
     *
     * @param fspRequest request with filter, paging and sorting
     * @return result of given request for given fspRequest
     */
    default FspResponse<T> findFsp(FspRequest fspRequest) {
        Specification<T> specification = new FilteringSpecification<>(fspRequest);
        FilteringAndSortingSpecification filteringAndSortingSpecification = new FilteringAndSortingSpecification(fspRequest);

        if (!fspRequest.hasPageInfo()) {
            List<T> list = findAll(specification, filteringAndSortingSpecification.getSort());
            return new FspResponse<>(list);
        }

        Page<T> listWithPagination = findAll(specification, filteringAndSortingSpecification.getPageRequest());
        long count = count(specification);
        return new FspResponse<>(fspRequest, listWithPagination.getContent(), count);
    }
}
