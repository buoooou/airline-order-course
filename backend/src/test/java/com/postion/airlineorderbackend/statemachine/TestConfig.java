package com.postion.airlineorderbackend.statemachine;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestConfiguration
@ComponentScan(basePackages = "com.postion.airlineorderbackend.statemachine")
@Import({OrderStateMachineConfig.class, OrderStateMachinePersisterConfig.class})
public class TestConfig {
}