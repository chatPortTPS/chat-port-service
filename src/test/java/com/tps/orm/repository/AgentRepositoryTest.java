package com.tps.orm.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class AgentRepositoryTest {

    @Inject
    AgentRepository agentRepository;

    private String testPublicId;
    private String testName;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up any existing test data
        agentRepository.deleteAll();
        
        testPublicId = UUID.randomUUID().toString();
        testName = "Test Agent " + System.currentTimeMillis();
        
        // Create test agent
        Agent agent = new Agent();
        agent.setPublicId(testPublicId);
        agent.setName(testName);
        agent.setDescription("Test Description");
        agent.setPrompt("Test Prompt");
        agent.setStatus(AgentStatus.DESARROLLO);
        agent.setTheme(AgentTheme.MINI);
        agent.setPosition(AgentPosition.BOTTOM_RIGHT);
        agent.setWebsite("https://test.com");
        agent.setType(AgentType.DYNAMIC);
        agent.setUserCreate("testuser");
        agent.setIntranet(true);
        agent.setCreatedAt(LocalDateTime.now());
        agent.setUpdatedAt(LocalDateTime.now());
        
        agentRepository.persist(agent);
    }

    @Test
    @Transactional
    void testFindByPublicId_Found() {
        List<Agent> agents = agentRepository.findByPublicId(testPublicId);
        
        assertNotNull(agents);
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testPublicId, agents.get(0).getPublicId());
    }

    @Test
    @Transactional
    void testFindByPublicId_NotFound() {
        List<Agent> agents = agentRepository.findByPublicId("non-existent-id");
        
        assertNotNull(agents);
        assertTrue(agents.isEmpty());
    }

    @Test
    @Transactional
    void testFindByName_Found() {
        List<Agent> agents = agentRepository.findByName(testName);
        
        assertNotNull(agents);
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testName, agents.get(0).getName());
    }

    @Test
    @Transactional
    void testFindByName_NotFound() {
        List<Agent> agents = agentRepository.findByName("Non-existent Agent");
        
        assertNotNull(agents);
        assertTrue(agents.isEmpty());
    }

    @Test
    @Transactional
    void testPersistAgent() {
        Agent newAgent = new Agent();
        newAgent.setPublicId(UUID.randomUUID().toString());
        newAgent.setName("New Test Agent");
        newAgent.setDescription("New Description");
        newAgent.setPrompt("New Prompt");
        newAgent.setStatus(AgentStatus.DESARROLLO);
        newAgent.setTheme(AgentTheme.MINI);
        newAgent.setPosition(AgentPosition.BOTTOM_RIGHT);
        newAgent.setWebsite("https://new-test.com");
        newAgent.setType(AgentType.DYNAMIC);
        newAgent.setUserCreate("newuser");
        newAgent.setIntranet(true);
        newAgent.setCreatedAt(LocalDateTime.now());
        newAgent.setUpdatedAt(LocalDateTime.now());
        
        agentRepository.persist(newAgent);
        
        assertNotNull(newAgent.getId());
        
        Agent found = agentRepository.findById(newAgent.getId());
        assertNotNull(found);
        assertEquals("New Test Agent", found.getName());
    }

    @Test
    @Transactional
    void testDeleteAgent() {
        List<Agent> agents = agentRepository.findByPublicId(testPublicId);
        assertFalse(agents.isEmpty());
        
        Agent agent = agents.get(0);
        agentRepository.delete(agent);
        
        Agent found = agentRepository.findById(agent.getId());
        assertNull(found);
    }

    @Test
    @Transactional
    void testListAll() {
        List<Agent> allAgents = agentRepository.listAll();
        
        assertNotNull(allAgents);
        assertTrue(allAgents.size() >= 1);
    }
}
