package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.model.FlightInfo;

@Service
public class FlightApiService {

    public FlightInfo getFlightInfo(String flightNumber) {
        // 模拟航班API返回数据
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(flightNumber);
        flightInfo.setDepartureCity("北京");
        flightInfo.setArrivalCity("上海");
        flightInfo.setDepartureTime(new java.sql.Timestamp(System.currentTimeMillis()));
        return flightInfo;
    }
}