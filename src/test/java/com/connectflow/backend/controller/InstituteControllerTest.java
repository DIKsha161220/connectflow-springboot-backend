package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.service.InstituteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstituteControllerTest {

    @Mock
    private InstituteService instituteService;

    @InjectMocks
    private InstituteController controller;

    @Test
    void returnsJsonSuccessEnvelopeForInstituteList() {
        when(instituteService.findVisibleTo(null)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<com.connectflow.backend.dto.InstituteResponse>>> response =
                controller.findAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());
        assertEquals(List.of(), response.getBody().data());
        verify(instituteService).findVisibleTo(null);
    }
}
