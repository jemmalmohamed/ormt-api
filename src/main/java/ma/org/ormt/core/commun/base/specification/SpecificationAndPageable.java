package ma.org.ormt.core.commun.base.specification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationAndPageable<E> {
    private final Specification<E> specification;
    private final Pageable pageable;

    public SpecificationAndPageable(Specification<E> specification, Pageable pageable) {
        this.specification = specification;
        this.pageable = pageable;
    }

    public Specification<E> getSpecification() {
        return specification;
    }

    public Pageable getPageable() {
        return pageable;
    }
}