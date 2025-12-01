package com.tps.orm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;
import com.tps.orm.entity.AreaAgente;
import com.tps.orm.repository.AgentRepository;
import com.tps.orm.repository.AreaAgenteRepository;

import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

class AgentServiceTest {

    @Mock
    AgentRepository agentRepository;

    @Mock
    AreaAgenteRepository areaAgenteRepository;

    @InjectMocks
    AgentService agentService;

    private Agent testAgent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testAgent = new Agent();
        testAgent.setId(1L);
        testAgent.setPublicId(UUID.randomUUID().toString());
        testAgent.setName("Test Agent");
        testAgent.setDescription("Test Description");
        testAgent.setPrompt("Test Prompt");
        testAgent.setStatus(AgentStatus.DESARROLLO);
        testAgent.setTheme(AgentTheme.MINI);
        testAgent.setPosition(AgentPosition.BOTTOM_RIGHT);
        testAgent.setWebsite("https://test.com");
        testAgent.setType(AgentType.DYNAMIC);
        testAgent.setUserCreate("testuser");
        testAgent.setIntranet(true);
        testAgent.setCreatedAt(LocalDateTime.now());
        testAgent.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateAgent_Success() {
        AgentRequest request = new AgentRequest();
        request.setName("New Agent");
        request.setDescription("New Description");
        request.setPrompt("New Prompt");
        request.setUserCreate("creator");
        request.setStatus(AgentStatus.DESARROLLO);
        request.setTheme(AgentTheme.MINI);
        request.setPosition(AgentPosition.BOTTOM_RIGHT);
        request.setWebsite("https://example.com");
        request.setType(AgentType.DYNAMIC);

        doNothing().when(agentRepository).persist(any(Agent.class));

        AgentResponse response = agentService.createAgent(request);

        assertNotNull(response);
        verify(agentRepository, times(1)).persist(any(Agent.class));
    }

    @Test
    void testCreateAgent_WithoutName_ThrowsException() {
        AgentRequest request = new AgentRequest();
        request.setName("");
        request.setUserCreate("creator");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.createAgent(request)
        );

        assertEquals("El nombre del agente no puede estar vacío", exception.getMessage());
    }

    @Test
    void testCreateAgent_WithoutUserCreate_ThrowsException() {
        AgentRequest request = new AgentRequest();
        request.setName("Test Agent");
        request.setUserCreate("");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.createAgent(request)
        );

        assertEquals("El creador del agente no puede estar vacío", exception.getMessage());
    }

    @Test
    void testCreateAgent_WithCentralChatPortName_ThrowsException() {
        AgentRequest request = new AgentRequest();
        request.setName("Central ChatPort");
        request.setUserCreate("creator");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.createAgent(request)
        );

        assertEquals("El nombre del agente no puede ser 'Central ChatPort'", exception.getMessage());
    }

    @Test
    void testGetCentralAgent_ExistingAgent() {
        when(agentRepository.findByName("Central ChatPort")).thenReturn(Arrays.asList(testAgent));

        AgentResponse response = agentService.getCentralAgent();

        assertNotNull(response);
        verify(agentRepository, times(1)).findByName("Central ChatPort");
        verify(agentRepository, never()).persist(any(Agent.class));
    }

    @Test
    void testGetCentralAgent_CreateNewIfNotExists() {
        when(agentRepository.findByName("Central ChatPort")).thenReturn(Collections.emptyList());
        doNothing().when(agentRepository).persist(any(Agent.class));

        AgentResponse response = agentService.getCentralAgent();

        assertNotNull(response);
        verify(agentRepository, times(1)).persist(any(Agent.class));
    }

    @Test
    void testGetAllAgents() {
        List<Agent> agents = Arrays.asList(testAgent);
        when(agentRepository.listAll()).thenReturn(agents);

        List<AgentResponse> responses = agentService.getAllAgents();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(agentRepository, times(1)).listAll();
    }

    @Test
    void testVincularAreaAlAgente_Success() {
        Long agentId = 1L;
        String areaPublicId = "area-uuid-123";

        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        when(areaAgenteRepository.findByAgentIdAndAreaPublicId(agentId, areaPublicId)).thenReturn(null);
        doNothing().when(areaAgenteRepository).persist(any(AreaAgente.class));

        assertDoesNotThrow(() -> agentService.vincularAreaAlAgente(agentId, areaPublicId));

        verify(areaAgenteRepository, times(1)).persist(any(AreaAgente.class));
    }

    @Test
    void testVincularAreaAlAgente_AgentNotFound() {
        Long agentId = 999L;
        String areaPublicId = "area-uuid-123";

        when(agentRepository.findById(agentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.vincularAreaAlAgente(agentId, areaPublicId)
        );

        assertEquals("Agente no encontrado con identificador proporcionado: " + agentId, exception.getMessage());
    }

    @Test
    void testVincularAreaAlAgente_AreaAlreadyLinked() {
        Long agentId = 1L;
        String areaPublicId = "area-uuid-123";
        AreaAgente existingArea = new AreaAgente();

        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        when(areaAgenteRepository.findByAgentIdAndAreaPublicId(agentId, areaPublicId)).thenReturn(existingArea);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.vincularAreaAlAgente(agentId, areaPublicId)
        );

        assertEquals("El área ya está vinculada al agente", exception.getMessage());
    }

    @Test
    void testDeleteAgentById_Success() {
        Long agentId = 1L;

        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        when(areaAgenteRepository.findByAgentId(agentId)).thenReturn(Collections.emptyList());
        doNothing().when(agentRepository).delete(testAgent);

        boolean result = agentService.deleteAgentById(agentId);

        assertTrue(result);
        verify(agentRepository, times(1)).delete(testAgent);
    }

    @Test
    void testDeleteAgentById_NotFound() {
        Long agentId = 999L;

        when(agentRepository.findById(agentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.deleteAgentById(agentId)
        );

        assertEquals("Agente no encontrado con ID: " + agentId, exception.getMessage());
    }

    @Test
    void testGetAgentByPublicId_Success() {
        String publicId = testAgent.getPublicId();
        when(agentRepository.findByPublicId(publicId)).thenReturn(Arrays.asList(testAgent));
        when(areaAgenteRepository.findByAgentId(testAgent.getId())).thenReturn(Collections.emptyList());

        List<AgentResponse> responses = agentService.getAgentByPublicId(publicId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(agentRepository, times(1)).findByPublicId(publicId);
    }

    @Test
    void testGetAgentByPublicId_NotFound() {
        String publicId = "non-existent-uuid";
        when(agentRepository.findByPublicId(publicId)).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.getAgentByPublicId(publicId)
        );

        assertEquals("Agente no encontrado con uuid: " + publicId, exception.getMessage());
    }

    @Test
    void testUpdateAgent_Success() {
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        doNothing().when(agentRepository).persist(testAgent);

        AgentResponse response = agentService.updateAgent(
            agentId,
            "Updated Name",
            "Updated Description",
            "Updated Prompt",
            "MINI",
            "BOTTOM_LEFT",
            "https://updated.com"
        );

        assertNotNull(response);
        verify(agentRepository, times(1)).persist(testAgent);
    }

    @Test
    void testUpdateAgent_NotFound() {
        Long agentId = 999L;
        when(agentRepository.findById(agentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.updateAgent(agentId, "Name", null, null, null, null, null)
        );

        assertEquals("Agente no encontrado con ID: " + agentId, exception.getMessage());
    }

    @Test
    void testPublishAgent_Success() {
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        doNothing().when(agentRepository).persist(testAgent);

        AgentResponse response = agentService.publishAgent(agentId);

        assertNotNull(response);
        assertEquals(AgentStatus.PUBLICADO, testAgent.getStatus());
        verify(agentRepository, times(1)).persist(testAgent);
    }

    @Test
    void testDeactivateAgent_Success() {
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        doNothing().when(agentRepository).persist(testAgent);

        AgentResponse response = agentService.deactivateAgent(agentId);

        assertNotNull(response);
        assertEquals(AgentStatus.DESACTIVADO, testAgent.getStatus());
        verify(agentRepository, times(1)).persist(testAgent);
    }

    @Test
    void testChangeIntranetStatus_Success() {
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(testAgent);
        doNothing().when(agentRepository).persist(testAgent);

        boolean result = agentService.changeIntranetStatus(agentId, false);

        assertTrue(result);
        assertFalse(testAgent.getIntranet());
        verify(agentRepository, times(1)).persist(testAgent);
    }

    @Test
    void testChangeIntranetStatus_AgentNotFound() {
        Long agentId = 999L;
        when(agentRepository.findById(agentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agentService.changeIntranetStatus(agentId, true)
        );

        assertEquals("Agente no encontrado con ID: " + agentId, exception.getMessage());
    }
}
