package com.airline.config;

import com.airline.entity.Airline;
import com.airline.entity.Airport;
import com.airline.entity.Flight;
import com.airline.repository.AirlineRepository;
import com.airline.repository.AirportRepository;
import com.airline.repository.FlightRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private AirportRepository airportRepository;
    
    @Autowired
    private AirlineRepository airlineRepository;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化基础数据...");
        
        initializeAirlines();
        initializeAirports();
        initializeFlights();
        
        logger.info("基础数据初始化完成");
    }
    
    private void initializeAirlines() {
        if (airlineRepository.count() == 0) {
            logger.info("初始化航空公司数据...");
            
            Airline ca = new Airline();
            ca.setCode("CA");
            ca.setName("中国国际航空");
            ca.setLogoUrl("/assets/images/airlines/ca.png");
            ca.setCountry("CN");
            airlineRepository.save(ca);
            
            Airline cz = new Airline();
            cz.setCode("CZ");
            cz.setName("中国南方航空");
            cz.setLogoUrl("/assets/images/airlines/cz.png");
            cz.setCountry("CN");
            airlineRepository.save(cz);
            
            Airline mu = new Airline();
            mu.setCode("MU");
            mu.setName("中国东方航空");
            mu.setLogoUrl("/assets/images/airlines/mu.png");
            mu.setCountry("CN");
            airlineRepository.save(mu);
            
            Airline hu = new Airline();
            hu.setCode("HU");
            hu.setName("海南航空");
            hu.setLogoUrl("/assets/images/airlines/hu.png");
            hu.setCountry("CN");
            airlineRepository.save(hu);
            
            logger.info("航空公司数据初始化完成，共插入 {} 条记录", airlineRepository.count());
        } else {
            logger.info("航空公司数据已存在，跳过初始化");
        }
    }
    
    private void initializeAirports() {
        if (airportRepository.count() == 0) {
            logger.info("初始化机场数据...");
            
            Airport pek = new Airport();
            pek.setCode("PEK");
            pek.setName("北京首都国际机场");
            pek.setCity("北京");
            pek.setCountry("CN");
            pek.setTimezone("Asia/Shanghai");
            airportRepository.save(pek);
            
            Airport sha = new Airport();
            sha.setCode("SHA");
            sha.setName("上海虹桥国际机场");
            sha.setCity("上海");
            sha.setCountry("CN");
            sha.setTimezone("Asia/Shanghai");
            airportRepository.save(sha);
            
            Airport pvg = new Airport();
            pvg.setCode("PVG");
            pvg.setName("上海浦东国际机场");
            pvg.setCity("上海");
            pvg.setCountry("CN");
            pvg.setTimezone("Asia/Shanghai");
            airportRepository.save(pvg);
            
            Airport can = new Airport();
            can.setCode("CAN");
            can.setName("广州白云国际机场");
            can.setCity("广州");
            can.setCountry("CN");
            can.setTimezone("Asia/Shanghai");
            airportRepository.save(can);
            
            Airport szx = new Airport();
            szx.setCode("SZX");
            szx.setName("深圳宝安国际机场");
            szx.setCity("深圳");
            szx.setCountry("CN");
            szx.setTimezone("Asia/Shanghai");
            airportRepository.save(szx);
            
            Airport ctu = new Airport();
            ctu.setCode("CTU");
            ctu.setName("成都双流国际机场");
            ctu.setCity("成都");
            ctu.setCountry("CN");
            ctu.setTimezone("Asia/Shanghai");
            airportRepository.save(ctu);
            
            Airport xiy = new Airport();
            xiy.setCode("XIY");
            xiy.setName("西安咸阳国际机场");
            xiy.setCity("西安");
            xiy.setCountry("CN");
            xiy.setTimezone("Asia/Shanghai");
            airportRepository.save(xiy);
            
            logger.info("机场数据初始化完成，共插入 {} 条记录", airportRepository.count());
        } else {
            logger.info("机场数据已存在，跳过初始化");
        }
    }
    
    private void initializeFlights() {
        // 临时强制重新初始化航班数据以测试新功能
        flightRepository.deleteAll();
        if (true) { // 强制重新初始化
            logger.info("初始化航班数据...");
            
            // 获取航空公司和机场
            Airline ca = airlineRepository.findByCode("CA").orElse(null);
            Airline cz = airlineRepository.findByCode("CZ").orElse(null);
            Airline mu = airlineRepository.findByCode("MU").orElse(null);
            Airline hu = airlineRepository.findByCode("HU").orElse(null);
            
            Airport pek = airportRepository.findByCode("PEK").orElse(null);
            Airport sha = airportRepository.findByCode("SHA").orElse(null);
            Airport pvg = airportRepository.findByCode("PVG").orElse(null);
            Airport can = airportRepository.findByCode("CAN").orElse(null);
            
            // 使用当前日期作为基准日期
            LocalDateTime baseDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            // 添加PEK到PVG的航班
            if (ca != null && pek != null && pvg != null) {
                // CA1501: 北京-上海浦东
                Flight flight1 = new Flight();
                flight1.setFlightNumber("CA1501");
                flight1.setAirline(ca);
                flight1.setDepartureAirport(pek);
                flight1.setArrivalAirport(pvg);
                flight1.setDepartureTime(baseDate.withHour(8).withMinute(0));
                flight1.setArrivalTime(baseDate.withHour(10).withMinute(30));
                flight1.setAircraftType("A320");
                flight1.setTotalSeats(180);
                flight1.setAvailableSeats(150);
                flight1.setEconomyPrice(new BigDecimal("800.00"));
                flight1.setBusinessPrice(new BigDecimal("2400.00"));
                flight1.setFirstPrice(new BigDecimal("4800.00"));
                flight1.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight1);
                
                // CA1503: 北京-上海浦东
                Flight flight1b = new Flight();
                flight1b.setFlightNumber("CA1503");
                flight1b.setAirline(ca);
                flight1b.setDepartureAirport(pek);
                flight1b.setArrivalAirport(pvg);
                flight1b.setDepartureTime(baseDate.withHour(16).withMinute(0));
                flight1b.setArrivalTime(baseDate.withHour(18).withMinute(30));
                flight1b.setAircraftType("A320");
                flight1b.setTotalSeats(180);
                flight1b.setAvailableSeats(150);
                flight1b.setEconomyPrice(new BigDecimal("850.00"));
                flight1b.setBusinessPrice(new BigDecimal("2550.00"));
                flight1b.setFirstPrice(new BigDecimal("5100.00"));
                flight1b.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight1b);
            }
            
            if (mu != null && pek != null && pvg != null) {
                // MU5103: 北京-上海浦东
                Flight flight2 = new Flight();
                flight2.setFlightNumber("MU5103");
                flight2.setAirline(mu);
                flight2.setDepartureAirport(pek);
                flight2.setArrivalAirport(pvg);
                flight2.setDepartureTime(baseDate.withHour(18).withMinute(30));
                flight2.setArrivalTime(baseDate.withHour(21).withMinute(0));
                flight2.setAircraftType("B737");
                flight2.setTotalSeats(160);
                flight2.setAvailableSeats(120);
                flight2.setEconomyPrice(new BigDecimal("780.00"));
                flight2.setBusinessPrice(new BigDecimal("2340.00"));
                flight2.setFirstPrice(new BigDecimal("4680.00"));
                flight2.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight2);
            }
            
            if (ca != null && pek != null && sha != null) {
                // CA1234: 北京-上海虹桥
                Flight flight3 = new Flight();
                flight3.setFlightNumber("CA1234");
                flight3.setAirline(ca);
                flight3.setDepartureAirport(pek);
                flight3.setArrivalAirport(sha);
                flight3.setDepartureTime(baseDate.withHour(14).withMinute(0));
                flight3.setArrivalTime(baseDate.withHour(16).withMinute(30));
                flight3.setAircraftType("A320");
                flight3.setTotalSeats(180);
                flight3.setAvailableSeats(120);
                flight3.setEconomyPrice(new BigDecimal("850.00"));
                flight3.setBusinessPrice(new BigDecimal("2550.00"));
                flight3.setFirstPrice(new BigDecimal("5100.00"));
                flight3.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight3);
            }
            
            if (cz != null && pek != null && can != null) {
                // CZ3456: 北京-广州
                Flight flight4 = new Flight();
                flight4.setFlightNumber("CZ3456");
                flight4.setAirline(cz);
                flight4.setDepartureAirport(pek);
                flight4.setArrivalAirport(can);
                flight4.setDepartureTime(baseDate.withHour(9).withMinute(30));
                flight4.setArrivalTime(baseDate.withHour(13).withMinute(0));
                flight4.setAircraftType("B737");
                flight4.setTotalSeats(160);
                flight4.setAvailableSeats(140);
                flight4.setEconomyPrice(new BigDecimal("1200.00"));
                flight4.setBusinessPrice(new BigDecimal("3600.00"));
                flight4.setFirstPrice(new BigDecimal("7200.00"));
                flight4.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight4);
            }
            
            if (mu != null && sha != null && can != null) {
                // MU5678: 上海-广州
                Flight flight5 = new Flight();
                flight5.setFlightNumber("MU5678");
                flight5.setAirline(mu);
                flight5.setDepartureAirport(sha);
                flight5.setArrivalAirport(can);
                flight5.setDepartureTime(baseDate.plusDays(1).withHour(11).withMinute(0));
                flight5.setArrivalTime(baseDate.plusDays(1).withHour(13).withMinute(30));
                flight5.setAircraftType("A321");
                flight5.setTotalSeats(200);
                flight5.setAvailableSeats(180);
                flight5.setEconomyPrice(new BigDecimal("900.00"));
                flight5.setBusinessPrice(new BigDecimal("2700.00"));
                flight5.setFirstPrice(new BigDecimal("5400.00"));
                flight5.setStatus(Flight.Status.SCHEDULED);
                flightRepository.save(flight5);
            }
            
            // 生成未来30天的北京至上海详细航班数据
            generateBeijingToShanghaiFlights(baseDate, ca, cz, mu, hu, pek, sha, pvg);
            
            logger.info("航班数据初始化完成，共插入 {} 条记录", flightRepository.count());
        } else {
            logger.info("航班数据已存在，跳过初始化");
        }
    }
    
    /**
     * 生成未来60天的多条热门航线航班数据
     */
    private void generateBeijingToShanghaiFlights(LocalDateTime baseDate, Airline ca, Airline cz, Airline mu, Airline hu, Airport pek, Airport sha, Airport pvg) {
        logger.info("开始生成未来60天的多条航线航班数据...");
        
        // 获取其他机场
        Airport can = airportRepository.findByCode("CAN").orElse(null);
        Airport szx = airportRepository.findByCode("SZX").orElse(null);
        Airport ctu = airportRepository.findByCode("CTU").orElse(null);
        Airport xiy = airportRepository.findByCode("XIY").orElse(null);
        
        // 生成多条航线的航班数据
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pek, pvg, "北京-上海浦东", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pek, sha, "北京-上海虹桥", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pek, can, "北京-广州", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pek, ctu, "北京-成都", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, sha, szx, "上海-深圳", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pvg, szx, "上海浦东-深圳", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, can, szx, "广州-深圳", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, can, ctu, "广州-成都", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, sha, can, "上海-广州", 60);
        generateFlightsForRoute(baseDate, ca, cz, mu, hu, pek, xiy, "北京-西安", 60);
        
        logger.info("所有航线航班数据生成完成");
    }
    
    /**
     * 为指定航线生成航班数据
     */
    private void generateFlightsForRoute(LocalDateTime baseDate, Airline ca, Airline cz, Airline mu, Airline hu, 
                                       Airport departureAirport, Airport arrivalAirport, String routeName, int days) {
        if (departureAirport == null || arrivalAirport == null) {
            logger.warn("跳过航线 {} - 机场信息不完整", routeName);
            return;
        }
        
        logger.info("生成航线 {} 的航班数据，共 {} 天", routeName, days);
        
        // 扩展的航班时刻表配置 (增加到8个时间段)
        int[][] flightSchedules = {
            {6, 30, 8, 45},   // 早班 06:30-08:45
            {8, 0, 10, 15},   // 上午 08:00-10:15
            {9, 30, 11, 45},  // 上午 09:30-11:45
            {11, 0, 13, 15},  // 上午 11:00-13:15
            {13, 30, 15, 45}, // 下午 13:30-15:45
            {15, 0, 17, 15},  // 下午 15:00-17:15
            {16, 30, 18, 45}, // 下午 16:30-18:45
            {18, 0, 20, 15},  // 晚班 18:00-20:15
            {19, 30, 21, 45}, // 晚班 19:30-21:45
            {21, 0, 23, 15}   // 晚班 21:00-23:15
        };
        
        // 航空公司配置
        Airline[] airlines = {ca, cz, mu, hu};
        String[] airlineCodes = {"CA", "CZ", "MU", "HU"};
        String[] aircraftTypes = {"A320", "A321", "B737", "B738", "A330", "B787"};
        
        int flightCount = 0;
        
        // 生成指定天数的航班
        for (int day = 0; day < days; day++) {
            LocalDateTime currentDate = baseDate.plusDays(day);
            
            // 判断是否为周末
            boolean isWeekend = isWeekend(currentDate);
            // 判断是否为节假日
            boolean isHoliday = isHoliday(currentDate);
            
            // 每天生成多个航班
            for (int scheduleIndex = 0; scheduleIndex < flightSchedules.length; scheduleIndex++) {
                int[] schedule = flightSchedules[scheduleIndex];
                
                // 为每个时间段生成不同航空公司的航班
                for (int airlineIndex = 0; airlineIndex < airlines.length; airlineIndex++) {
                    Airline airline = airlines[airlineIndex];
                    if (airline == null) continue;
                    
                    // 增加航班密度，减少跳过的概率
                    if ((day + scheduleIndex + airlineIndex) % 4 == 0) continue;
                    
                    // 节假日增加航班
                    if (isHoliday && (day + scheduleIndex + airlineIndex) % 2 == 0) {
                        // 节假日期间增加额外航班
                    }
                    
                    Flight flight = new Flight();
                    
                    // 生成航班号
                    String flightNumber = generateFlightNumber(airlineCodes[airlineIndex], scheduleIndex, day, 
                                                             departureAirport.getCode(), arrivalAirport.getCode());
                    flight.setFlightNumber(flightNumber);
                    
                    flight.setAirline(airline);
                    flight.setDepartureAirport(departureAirport);
                    flight.setArrivalAirport(arrivalAirport);
                    
                    // 设置起飞和到达时间（根据航线距离调整飞行时间）
                    int[] adjustedSchedule = adjustFlightTimeByRoute(schedule, departureAirport.getCode(), arrivalAirport.getCode());
                    LocalDateTime departureTime = currentDate.withHour(adjustedSchedule[0]).withMinute(adjustedSchedule[1]);
                    LocalDateTime arrivalTime = currentDate.withHour(adjustedSchedule[2]).withMinute(adjustedSchedule[3]);
                    
                    // 跨日航班处理
                    if (arrivalTime.isBefore(departureTime)) {
                        arrivalTime = arrivalTime.plusDays(1);
                    }
                    
                    flight.setDepartureTime(departureTime);
                    flight.setArrivalTime(arrivalTime);
                    
                    // 随机选择机型
                    String aircraftType = aircraftTypes[(scheduleIndex + airlineIndex + day) % aircraftTypes.length];
                    flight.setAircraftType(aircraftType);
                    
                    // 根据机型设置座位数
                    int totalSeats = getTotalSeatsByAircraftType(aircraftType);
                    flight.setTotalSeats(totalSeats);
                    
                    // 根据日期类型调整可用座位数
                    double availabilityRate = 0.7 + Math.random() * 0.3; // 基础70%-100%
                    if (isHoliday) {
                        availabilityRate *= 0.8; // 节假日座位更紧张
                    } else if (isWeekend) {
                        availabilityRate *= 0.9; // 周末座位稍紧张
                    }
                    flight.setAvailableSeats((int)(totalSeats * availabilityRate));
                    
                    // 设置价格（根据时间段、日期、机场、航线距离有所变化）
                    BigDecimal basePrice = getBasePriceForRoute(departureAirport.getCode(), arrivalAirport.getCode(), 
                                                              scheduleIndex, day, isWeekend, isHoliday);
                    flight.setEconomyPrice(basePrice);
                    flight.setBusinessPrice(basePrice.multiply(new BigDecimal("3.0")));
                    flight.setFirstPrice(basePrice.multiply(new BigDecimal("6.0")));
                    
                    flight.setStatus(Flight.Status.SCHEDULED);
                    
                    flightRepository.save(flight);
                    flightCount++;
                }
            }
        }
        
        logger.info("航线 {} 航班数据生成完成，共生成 {} 条航班记录", routeName, flightCount);
    }
    
    /**
     * 根据机型获取总座位数
     */
    private int getTotalSeatsByAircraftType(String aircraftType) {
        switch (aircraftType) {
            case "A320": return 180;
            case "A321": return 220;
            case "B737": return 160;
            case "B738": return 175;
            case "A330": return 280;
            case "B787": return 250;
            default: return 180;
        }
    }
    
    /**
     * 生成航班号
     */
    private String generateFlightNumber(String airlineCode, int scheduleIndex, int day, String depCode, String arrCode) {
        // 根据航线生成不同的航班号段
        int baseNumber = getFlightNumberBase(depCode, arrCode);
        int flightNumber = baseNumber + (scheduleIndex * 10) + (day % 10);
        return String.format("%s%d", airlineCode, flightNumber);
    }
    
    /**
     * 根据航线获取航班号基数
     */
    private int getFlightNumberBase(String depCode, String arrCode) {
        String route = depCode + "-" + arrCode;
        switch (route) {
            case "PEK-PVG": case "PEK-SHA": return 1800;
            case "PEK-CAN": return 1300;
            case "PEK-CTU": return 4100;
            case "PEK-XIY": return 2100;
            case "SHA-SZX": case "PVG-SZX": return 9500;
            case "SHA-CAN": return 9200;
            case "CAN-SZX": return 3900;
            case "CAN-CTU": return 3400;
            default: return 8000;
        }
    }
    
    /**
     * 根据航线调整飞行时间
     */
    private int[] adjustFlightTimeByRoute(int[] originalSchedule, String depCode, String arrCode) {
        int[] adjustedSchedule = originalSchedule.clone();
        
        // 根据航线距离调整飞行时间
        int flightDuration = getFlightDuration(depCode, arrCode);
        
        // 重新计算到达时间
        int depHour = originalSchedule[0];
        int depMinute = originalSchedule[1];
        
        int totalMinutes = depHour * 60 + depMinute + flightDuration;
        int arrHour = (totalMinutes / 60) % 24;
        int arrMinute = totalMinutes % 60;
        
        adjustedSchedule[2] = arrHour;
        adjustedSchedule[3] = arrMinute;
        
        return adjustedSchedule;
    }
    
    /**
     * 获取航线飞行时长（分钟）
     */
    private int getFlightDuration(String depCode, String arrCode) {
        String route = depCode + "-" + arrCode;
        switch (route) {
            case "PEK-PVG": case "PEK-SHA": return 135; // 2小时15分
            case "PEK-CAN": return 195; // 3小时15分
            case "PEK-CTU": return 165; // 2小时45分
            case "PEK-XIY": return 120; // 2小时
            case "SHA-SZX": case "PVG-SZX": return 150; // 2小时30分
            case "SHA-CAN": return 135; // 2小时15分
            case "CAN-SZX": return 75;  // 1小时15分
            case "CAN-CTU": return 135; // 2小时15分
            default: return 120; // 默认2小时
        }
    }
    
    /**
     * 判断是否为周末
     */
    private boolean isWeekend(LocalDateTime date) {
        java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY;
    }
    
    /**
     * 判断是否为节假日（简化版本）
     */
    private boolean isHoliday(LocalDateTime date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        // 简化的节假日判断
        // 国庆节期间 (10月1-7日)
        if (month == 10 && day >= 1 && day <= 7) return true;
        // 春节期间 (假设2月10-16日)
        if (month == 2 && day >= 10 && day <= 16) return true;
        // 五一期间 (5月1-3日)
        if (month == 5 && day >= 1 && day <= 3) return true;
        
        return false;
    }
    
    /**
     * 根据航线获取基础价格
     */
    private BigDecimal getBasePriceForRoute(String depCode, String arrCode, int scheduleIndex, int day, boolean isWeekend, boolean isHoliday) {
        // 根据航线设置基础价格
        BigDecimal basePrice = getRouteBasePrice(depCode, arrCode);
        
        // 根据时间段调整价格
        if (scheduleIndex < 2 || scheduleIndex >= 8) {
            basePrice = basePrice.multiply(new BigDecimal("0.85")); // 早班和晚班85折
        } else if (scheduleIndex >= 4 && scheduleIndex < 7) {
            basePrice = basePrice.multiply(new BigDecimal("1.15")); // 下午黄金时段115%
        }
        
        // 根据日期类型调整价格
        if (isHoliday) {
            basePrice = basePrice.multiply(new BigDecimal("1.5")); // 节假日150%
        } else if (isWeekend) {
            basePrice = basePrice.multiply(new BigDecimal("1.2")); // 周末120%
        }
        
        // 添加随机波动
        double randomFactor = 0.9 + Math.random() * 0.2; // 90%-110%的随机波动
        basePrice = basePrice.multiply(new BigDecimal(randomFactor));
        
        return basePrice.setScale(0, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * 根据航线获取基础价格
     */
    private BigDecimal getRouteBasePrice(String depCode, String arrCode) {
        String route = depCode + "-" + arrCode;
        switch (route) {
            case "PEK-PVG": return new BigDecimal("850");
            case "PEK-SHA": return new BigDecimal("800");
            case "PEK-CAN": return new BigDecimal("1200");
            case "PEK-CTU": return new BigDecimal("1000");
            case "PEK-XIY": return new BigDecimal("700");
            case "SHA-SZX": case "PVG-SZX": return new BigDecimal("900");
            case "SHA-CAN": return new BigDecimal("800");
            case "CAN-SZX": return new BigDecimal("400");
            case "CAN-CTU": return new BigDecimal("800");
            default: return new BigDecimal("800");
        }
    }
    
    /**
     * 根据目标机场、时间段和日期计算基础价格
     */
    private BigDecimal getBasePrice(String airportCode, int scheduleIndex, int dayOffset) {
        // 基础价格
        BigDecimal basePrice = new BigDecimal("750.00");
        
        // 浦东机场价格稍高
        if ("PVG".equals(airportCode)) {
            basePrice = basePrice.add(new BigDecimal("50.00"));
        }
        
        // 早班和晚班价格稍低
        if (scheduleIndex <= 1 || scheduleIndex >= 7) {
            basePrice = basePrice.subtract(new BigDecimal("30.00"));
        }
        
        // 黄金时段价格稍高
        if (scheduleIndex >= 3 && scheduleIndex <= 6) {
            basePrice = basePrice.add(new BigDecimal("50.00"));
        }
        
        // 周末价格稍高
        if (dayOffset % 7 == 5 || dayOffset % 7 == 6) {
            basePrice = basePrice.add(new BigDecimal("80.00"));
        }
        
        // 添加一些随机波动
        int randomAdjustment = (int)(Math.random() * 100) - 50; // -50到+50的随机调整
        basePrice = basePrice.add(new BigDecimal(randomAdjustment));
        
        // 确保价格不低于500元
        if (basePrice.compareTo(new BigDecimal("500.00")) < 0) {
            basePrice = new BigDecimal("500.00");
        }
        
        return basePrice;
    }
}