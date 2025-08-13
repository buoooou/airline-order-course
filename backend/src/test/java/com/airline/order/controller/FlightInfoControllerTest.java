package com.airline.order.controller;

import com.airline.order.dto.FlightInfoDTO;
import com.airline.order.service.FlightInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FlightInfoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FlightInfoService flightInfoService;
    
    private ObjectMapper objectMapper;
    private FlightInfoDTO testFlight;
    private List<FlightInfoDTO> testFlights;
    
    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // 创建测试航班数据
        testFlight = new FlightInfoDTO();
        testFlight.setId(1L);
        testFlight.setFlightNumber("CA1234");
        testFlight.setDepartureAirportCode("PEK");
        testFlight.setDepartureAirportName("北京首都国际机场");
        testFlight.setArrivalAirportCode("SHA");
        testFlight.setArrivalAirportName("上海虹桥国际机场");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testFlight.setFlightDuration(120);
        
        FlightInfoDTO testFlight2 = new FlightInfoDTO();
        testFlight2.setId(2L);
        testFlight2.setFlightNumber("MU5678");
        testFlight2.setDepartureAirportCode("PEK");
        testFlight2.setDepartureAirportName("北京首都国际机场");
        testFlight2.setArrivalAirportCode("CAN");
        testFlight2.setArrivalAirportName("广州白云国际机场");
        testFlight2.setDepartureTime(LocalDateTime.now().plusDays(2));
        testFlight2.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(3));
        testFlight2.setFlightDuration(180);
        
        testFlights = Arrays.asList(testFlight, testFlight2);
    }
    
    @Test
    public void testGetAllFlights() throws Exception {
        when(flightInfoService.getAllFlights()).thenReturn(testFlights);
        
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取所有航班成功"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("CA1234"))
                .andExpect(jsonPath("$.data[1].flightNumber").value("MU5678"));
    }
    
    @Test
    public void testGetFlightById() throws Exception {
        when(flightInfoService.getFlightById(1L)).thenReturn(testFlight);
        
        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取航班成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.flightNumber").value("CA1234"));
    }
    
    @Test
    public void testGetFlightByNumber() throws Exception {
        when(flightInfoService.getFlightByNumber("CA1234")).thenReturn(testFlight);
        
        mockMvc.perform(get("/api/flights/number/CA1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取航班成功"))
                .andExpect(jsonPath("$.data.flightNumber").value("CA1234"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateFlight() throws Exception {
        when(flightInfoService.createFlight(any(FlightInfoDTO.class))).thenReturn(testFlight);
        
        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlight)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班创建成功"))
                .andExpect(jsonPath("$.data.flightNumber").value("CA1234"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateFlight() throws Exception {
        when(flightInfoService.updateFlight(eq(1L), any(FlightInfoDTO.class))).thenReturn(testFlight);
        
        mockMvc.perform(put("/api/flights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlight)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班更新成功"))
                .andExpect(jsonPath("$.data.flightNumber").value("CA1234"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteFlight() throws Exception {
        doNothing().when(flightInfoService).deleteFlight(1L);
        
        mockMvc.perform(delete("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班删除成功"));
    }
    
    @Test
    public void testSearchFlightsByRoute() throws Exception {
        when(flightInfoService.searchFlightsByRoute("PEK", "SHA")).thenReturn(Arrays.asList(testFlight));
        
        mockMvc.perform(get("/api/flights/search/route")
                .param("departureCode", "PEK")
                .param("arrivalCode", "SHA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班查询成功"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("CA1234"));
    }
    
    @Test
    public void testSearchFlightsByDate() throws Exception {
        LocalDateTime testDate = LocalDateTime.now().plusDays(1);
        when(flightInfoService.searchFlightsByDate(any(LocalDateTime.class))).thenReturn(Arrays.asList(testFlight));
        
        mockMvc.perform(get("/api/flights/search/date")
                .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班查询成功"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("CA1234"));
    }
    
    @Test
    public void testSearchFlightsByNumberKeyword() throws Exception {
        when(flightInfoService.searchFlightsByNumberKeyword("CA")).thenReturn(Arrays.asList(testFlight));
        
        mockMvc.perform(get("/api/flights/search/number")
                .param("keyword", "CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班查询成功"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("CA1234"));
    }
    
    @Test
    public void testSearchFlightsByDuration() throws Exception {
        when(flightInfoService.searchFlightsByDuration(100, 150)).thenReturn(Arrays.asList(testFlight));
        
        mockMvc.perform(get("/api/flights/search/duration")
                .param("minDuration", "100")
                .param("maxDuration", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("航班查询成功"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].flightNumber").value("CA1234"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void testCreateFlightWithoutAdminRole() throws Exception {
        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlight)))
                .andExpect(status().isForbidden());
    }
}