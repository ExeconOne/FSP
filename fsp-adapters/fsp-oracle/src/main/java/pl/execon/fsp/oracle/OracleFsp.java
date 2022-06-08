package pl.execon.fsp.oracle;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;

import java.util.List;

public interface OracleFsp<T> extends JpaSpecificationExecutor<T> {

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
