package pl.execon.fsp.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FspInnerObjTestRepository extends JpaRepository<InnerTestObj, Long> {
}
