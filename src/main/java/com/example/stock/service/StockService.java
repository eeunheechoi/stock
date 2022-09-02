package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    //@Transactional
    // synchronized <-- 1개의 스레드만 접근 , @Transactional 을 사용하 스프링이 클래스를 새로 만들어 트랜잭션을 관리하기 때문에 업데이트 되기 전에 다른 스레드가 메서드 실행. synchronized는 transactional 과 같이 쓰면 소용 없음
    // 인스턴스를 여러개 사용하면 synchronized 또한 소용 없음
    @Transactional(propagation = Propagation.REQUIRES_NEW)// 부모의 트랜잭션과 별도 새로운 트랜잭션을 생성하는 옵셥
    public synchronized void decress(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.save(stock);
    }
}
