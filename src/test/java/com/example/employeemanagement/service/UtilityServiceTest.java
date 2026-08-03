package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UtilityServiceTest {

    private final UtilityService utilityService = new UtilityService();

    @Test
    void formatStringTrimsAndCollapsesWhitespace() {
        assertThat(utilityService.formatString("  Ada   Lovelace  ")).isEqualTo("Ada Lovelace");
        assertThat(utilityService.formatString(null)).isEmpty();
    }

    @Test
    void generateEmployeeCodeProducesSequentialCodes() {
        assertThat(utilityService.generateEmployeeCode()).isEqualTo("EMP-00001");
        assertThat(utilityService.generateEmployeeCode()).isEqualTo("EMP-00002");
    }
}
