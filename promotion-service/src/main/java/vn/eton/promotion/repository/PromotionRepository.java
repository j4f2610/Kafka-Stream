package vn.eton.promotion.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.eton.promotion.domain.Promotion;

@Repository
public interface PromotionRepository extends CrudRepository<Promotion, Long> {
}
