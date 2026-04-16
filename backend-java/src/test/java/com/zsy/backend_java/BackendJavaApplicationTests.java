package com.zsy.backend_java;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@MapperScan("com.zsy.backend_java.mapper")
@SpringBootTest
class BackendJavaApplicationTests {

	@Test
	void contextLoads() {
	}

}
