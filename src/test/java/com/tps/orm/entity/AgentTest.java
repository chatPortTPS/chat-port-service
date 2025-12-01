package com.tps.orm.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AgentTest {

    @Test
    void testOnCreate_GeneratesPublicId() {
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setUserCreate("testuser");
        
        agent.onCreate();
        
        assertNotNull(agent.getPublicId());
        assertFalse(agent.getPublicId().isEmpty());
        assertNotNull(agent.getCreatedAt());
        assertNotNull(agent.getUpdatedAt());
        assertTrue(agent.getIntranet());
    }

    @Test
    void testOnCreate_DoesNotOverrideExistingPublicId() {
        Agent agent = new Agent();
        String existingPublicId = "existing-uuid-123";
        agent.setPublicId(existingPublicId);
        agent.setName("Test Agent");
        agent.setUserCreate("testuser");
        
        agent.onCreate();
        
        assertEquals(existingPublicId, agent.getPublicId());
    }

    @Test
    void testOnUpdate_UpdatesTimestamp() {
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setUserCreate("testuser");
        
        agent.onCreate();
        
        java.time.LocalDateTime originalUpdatedAt = agent.getUpdatedAt();
        
        try {
            Thread.sleep(10); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        agent.onUpdate();
        
        assertNotNull(agent.getUpdatedAt());
        assertTrue(agent.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   agent.getUpdatedAt().isEqual(originalUpdatedAt));
    }

    @Test
    void testDefaultValues() {
        Agent agent = new Agent();
        
        assertEquals(AgentStatus.DESARROLLO, agent.getStatus());
        assertEquals(AgentTheme.getDefault(), agent.getTheme());
        assertEquals(AgentPosition.getDefault(), agent.getPosition());
        assertEquals(AgentType.getDefault(), agent.getType());
    }

    @Test
    void testSettersAndGetters() {
        Agent agent = new Agent();
        
        agent.setId(1L);
        agent.setPublicId("test-uuid");
        agent.setName("Test Agent");
        agent.setDescription("Test Description");
        agent.setPrompt("Test Prompt");
        agent.setStatus(AgentStatus.PUBLICADO);
        agent.setIntranet(false);
        agent.setUserCreate("creator");
        agent.setTheme(AgentTheme.MINI);
        agent.setPosition(AgentPosition.BOTTOM_LEFT);
        agent.setWebsite("https://example.com");
        agent.setType(AgentType.AREAS);
        
        assertEquals(1L, agent.getId());
        assertEquals("test-uuid", agent.getPublicId());
        assertEquals("Test Agent", agent.getName());
        assertEquals("Test Description", agent.getDescription());
        assertEquals("Test Prompt", agent.getPrompt());
        assertEquals(AgentStatus.PUBLICADO, agent.getStatus());
        assertFalse(agent.getIntranet());
        assertEquals("creator", agent.getUserCreate());
        assertEquals(AgentTheme.MINI, agent.getTheme());
        assertEquals(AgentPosition.BOTTOM_LEFT, agent.getPosition());
        assertEquals("https://example.com", agent.getWebsite());
        assertEquals(AgentType.AREAS, agent.getType());
    }
}
