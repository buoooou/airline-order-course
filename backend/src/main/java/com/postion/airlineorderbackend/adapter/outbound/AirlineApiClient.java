package com.postion.airlineorderbackend.adapter.outbound;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class AirlineApiClient {

	public String issueTicket(Long orderID) throws InterruptedException{
		System.out.println("开始为订单"+ orderID + "调用航司接口出票 " );
		//模拟网络延迟
		TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
		//模拟接口成功率
		if(ThreadLocalRandom.current().nextInt(10) < 8 ){
			System.out.println("订单" + orderID + "出票成功");
			return "TKT" + System.currentTimeMillis();
		}else{
			System.err.println("订单" + orderID + "出票失败：航司返回错误");
			throw new RuntimeException("Airline API error");
		}
	}
}
