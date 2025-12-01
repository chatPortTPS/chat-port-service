package com.tps.orm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tps.orm.entity.Area;
import com.tps.orm.repository.AreaRepository;

import services.operacion.area.dto.AreaResponse;

class AreaServiceTest {

    @Mock
    AreaRepository areaRepository;

    @InjectMocks
    AreaService areaService;

    private Area testArea;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testArea = new Area();
        testArea.setId(1L);
        testArea.setPublicId("test-public-id");
        testArea.setName("Test Area");
    }

    @Test
    void testSave_NewArea() {
        String name = "New Area";
        String publicId = "new-public-id";
        
        when(areaRepository.getAreaByPublicId(publicId)).thenReturn(null);
        doNothing().when(areaRepository).persist(any(Area.class));
        
        areaService.save(name, publicId);
        
        verify(areaRepository, times(1)).getAreaByPublicId(publicId);
        verify(areaRepository, times(1)).persist(any(Area.class));
    }

    @Test
    void testSave_UpdateExistingArea() {
        String name = "Updated Area";
        String publicId = testArea.getPublicId();
        
        when(areaRepository.getAreaByPublicId(publicId)).thenReturn(testArea);
        doNothing().when(areaRepository).persist(testArea);
        
        areaService.save(name, publicId);
        
        verify(areaRepository, times(1)).getAreaByPublicId(publicId);
        verify(areaRepository, times(1)).persist(testArea);
        assertEquals(name, testArea.getName());
    }

    @Test
    void testGetAllAreas() {
        Area area1 = new Area();
        area1.setId(1L);
        area1.setPublicId("public-id-1");
        area1.setName("Area 1");
        
        Area area2 = new Area();
        area2.setId(2L);
        area2.setPublicId("public-id-2");
        area2.setName("Area 2");
        
        List<Area> areas = Arrays.asList(area1, area2);
        
        when(areaRepository.listAll()).thenReturn(areas);
        
        List<AreaResponse> responses = areaService.getAllAreas();
        
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Area 1", responses.get(0).getName());
        assertEquals("Area 2", responses.get(1).getName());
        assertEquals("public-id-1", responses.get(0).getPublicId());
        assertEquals("public-id-2", responses.get(1).getPublicId());
        
        verify(areaRepository, times(1)).listAll();
    }

    @Test
    void testGetAllAreas_EmptyList() {
        when(areaRepository.listAll()).thenReturn(Arrays.asList());
        
        List<AreaResponse> responses = areaService.getAllAreas();
        
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(areaRepository, times(1)).listAll();
    }
}
