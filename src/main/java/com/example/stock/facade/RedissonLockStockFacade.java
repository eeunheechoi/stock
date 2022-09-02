package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {
    private RedissonClient redissonClient;
    private StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString());
        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decress(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

            lock.unlock();
        }
    }

    public void decrease1(final String key, final int count){
        final String lockName = key + ":lock";
        final RLock lock = redissonClient.getLock(lockName);
        final String worker = Thread.currentThread().getName();

        try {
            if(!lock.tryLock(1, 3, TimeUnit.SECONDS))
                return;

            final int stock = currentStock(key);
            if(stock <= EMPTY){
                log.info("[{}] 현재 남은 재고가 없습니다. ({}개)", worker, stock);
                return;
            }

            log.info("현재 진행중인 사람 : {} & 현재 남은 재고 : {}개", worker, stock);
            setStock(key, stock - count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

}
