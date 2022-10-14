package pl.piomin.payment.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.piomin.payment.domain.Point;
import pl.piomin.payment.repository.PointRepository;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final String SOURCE = "stock";
    private static final Logger LOG = LoggerFactory.getLogger(PointService.class);
    private final PointRepository repository;

    public Point create(Point model) {
        return repository.save(model);
    }

    public Iterable<Point> getList() {
        return repository.findAll();
    }

    public Point findById(Long id) {
        return repository.findById(id).get();
    }

}
