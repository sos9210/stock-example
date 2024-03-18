package com.example.stock.service;


import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     *     synchronized를 사용해도 @Transactional사용시 경쟁상태가 발생한다.
     *     @Transactional을 제거하고 synchronized를 사용한다 해도 대부분 서버2대 이상을 사용함으로써 경쟁상태 방지가 어렵다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW) //NamedLock사용시 별도의 트랜잭션으로 실행이 필요하다
    public /*synchronized*/ void decrease(Long id, Long quantity){
        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고를 감소
        stock.decrease(quantity);

        // 갱신된 값을 저장
        stockRepository.saveAndFlush(stock);
    }
}

