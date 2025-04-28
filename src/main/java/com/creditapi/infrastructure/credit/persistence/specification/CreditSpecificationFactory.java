package com.creditapi.infrastructure.credit.persistence.specification;

import com.creditapi.domain.credit.model.CreditFilter;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CreditSpecificationFactory {

  public static Specification<CreditEntity> build(CreditFilter filter) {
    return (root, query, cb) -> {
      var predicates = cb.conjunction();

      if (filter.getNfseNumber() != null) {
        predicates.getExpressions().add(cb.equal(root.get("nfseNumber"), filter.getNfseNumber()));
      }

      if (filter.getCreditNumber() != null) {
        predicates
            .getExpressions()
            .add(cb.equal(root.get("creditNumber"), filter.getCreditNumber()));
      }

      if (filter.getCreatedAfter() != null) {
        predicates
            .getExpressions()
            .add(cb.greaterThanOrEqualTo(root.get("constitutionDate"), filter.getCreatedAfter()));
      }

      if (filter.getCreatedBefore() != null) {
        predicates
            .getExpressions()
            .add(cb.lessThanOrEqualTo(root.get("constitutionDate"), filter.getCreatedBefore()));
      }

      if (filter.getUserId() != null) {
        predicates
            .getExpressions()
            .add(cb.equal(root.join("user", JoinType.INNER).get("id"), filter.getUserId()));
      }

      return predicates;
    };
  }
}
