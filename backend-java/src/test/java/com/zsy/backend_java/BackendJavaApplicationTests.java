package com.zsy.backend_java;

import static com.zsy.backend_java.util.RedisConstants.CACHE_SHOP_KEY;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import com.zsy.backend_java.entity.Shop;
import com.zsy.backend_java.service.IShopService;
import com.zsy.backend_java.service.impl.ShopServiceImpl;
import com.zsy.backend_java.util.CacheClient;

import jakarta.annotation.Resource;

@MapperScan("com.zsy.backend_java.mapper")
@SpringBootTest
class BackendJavaApplicationTests {

	@Resource
    private IShopService shopService;
	@Resource
	private ShopServiceImpl shopServiceImpl;
    @Resource
    private CacheClient cacheClient;
    
    @Test
    void testQueryWithMutex() throws InterruptedException {
        int threadCount = 20;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            es.submit(() -> {
                try {
                    shopService.queryById(1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        es.shutdown();
    }

	@Test
	void testSaveShop() throws InterruptedException {
		// shopServiceImpl.saveSho2Redis(2L, 10L);
        Shop shop = shopService.getById(1L);
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 1L, shop, 10L, TimeUnit.SECONDS);
	}
}
