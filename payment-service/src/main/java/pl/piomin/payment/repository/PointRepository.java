package pl.piomin.payment.repository;

import org.springframework.data.repository.CrudRepository;
import pl.piomin.payment.domain.Point;

public interface PointRepository extends CrudRepository<Point, Long> {
}
