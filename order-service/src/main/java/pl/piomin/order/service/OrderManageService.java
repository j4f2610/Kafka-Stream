package pl.piomin.order.service;

import org.springframework.stereotype.Service;
import pl.piomin.base.domain.Order;

import static org.apache.kafka.streams.state.RocksDBConfigSetter.LOG;

@Service
public class OrderManageService {

    public Order result(Order orderPayment, Order orderStock) {
        LOG.info("Output PAYMENT STREAM: {}", orderPayment);
        LOG.info("Output STOCK STREAM: {}", orderStock);
        return orderPayment;
    }
}
