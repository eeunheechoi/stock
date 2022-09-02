package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.OptimisticLockStockService;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NamedLockStockFacade {
    private LockRepository lockRepository;
    private StockService stockService;

    public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity)  {
        try {
            lockRepository.getLock(id.toString());
            stockService.decress(id, quantity);

        }finally {

            lockRepository.release(id.toString());

        }
    }
}
