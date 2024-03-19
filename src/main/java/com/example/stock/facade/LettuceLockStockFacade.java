package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

//redisson과 lettuce
// 재시도가 필요하지않으면 lettuce, 재시도가 필요하면 redisson을 선택을 고려해보자
@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        //스핀락 방식이므로 Thread.sleep을 통해 부하를 줄여준다
        while (!redisLockRepository.lock(id)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease(id,quantity);
        } finally {
            redisLockRepository.unlock(id);
        }
    }
}
