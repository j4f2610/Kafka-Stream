package pl.piomin.payment.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.piomin.payment.domain.Point;
import pl.piomin.payment.service.PointService;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger LOG = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;
    private AtomicLong id = new AtomicLong();
    @PostMapping
    public Point create(@RequestBody Point point) {
        point.setId(id.incrementAndGet());
        var pro = pointService.create(point);
        LOG.info("Create point: {}", pro);
        return pro;
    }

    @GetMapping
    public Iterable<Point> all() {
        return pointService.getList();
    }
}
