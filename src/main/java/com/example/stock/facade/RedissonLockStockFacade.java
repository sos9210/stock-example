package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
//redisson과 lettuce
// 재시도가 필요하지않으면 lettuce, 재시도가 필요하면 redisson을 선택을 고려해보자
@Component
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;
    private final StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }
    //알림이 오면 그때 1번만 락획득을 시도
    public void decrease(Long id, Long quantity) {
        RLock lock = redissonClient.getLock(id.toString());
        try {
            //락 획득과 재시도는 tryLock() 내부에서 이루어진다.
            //몇초동안 락획득을 시도할것인지 , 몇초동안 점유할것인지
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if(!available) {
                System.out.println("lock 획득 실패");
                return;
            }
            stockService.decrease(id,quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
