package eu.drus.test.persistence.rule.cache;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.test.persistence.core.metadata.FeatureResolver;
import eu.drus.test.persistence.rule.context.EntityManagerFactoryProducer;

@RunWith(MockitoJUnitRunner.class)
public class SecondLevelCacheStatementTest {

    @Mock
    private FeatureResolver resolver;

    @Mock
    private EntityManagerFactoryProducer emfProducer;

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private Cache cache;

    @Mock
    private Statement base;

    @Before
    public void setupMocks() {
        when(emfProducer.createEntityManagerFactory()).thenReturn(emf);
        when(emf.getCache()).thenReturn(cache);
    }

    @Test
    public void testEvictionOfSecondLevelCacheIsDisabled() throws Throwable {
        // GIVEN
        final SecondLevelCacheStatement stmt = new SecondLevelCacheStatement(resolver, emfProducer, base);

        // WHEN
        stmt.evaluate();

        // THEN
        verify(emfProducer).createEntityManagerFactory();
        verify(base).evaluate();
        verify(cache, times(0)).evictAll();
        verify(emfProducer).destroyEntityManagerFactory(emf);
    }

    @Test
    public void testEvictionOfSecondLevelCacheIsRunBeforeBaseStatementExecution() throws Throwable {
        // GIVEN
        when(resolver.shouldCleanupBefore()).thenReturn(Boolean.TRUE);
        when(resolver.shouldEvictCache()).thenReturn(Boolean.TRUE);
        final SecondLevelCacheStatement stmt = new SecondLevelCacheStatement(resolver, emfProducer, base);

        // WHEN
        stmt.evaluate();

        // THEN
        final InOrder order = inOrder(base, cache);
        verify(emfProducer).createEntityManagerFactory();
        order.verify(cache).evictAll();
        order.verify(base).evaluate();
        verify(emfProducer).destroyEntityManagerFactory(emf);
    }

    @Test
    public void testEvictionOfSecondLevelCacheIsRunAfterBaseStatementExecution() throws Throwable {
        // GIVEN
        when(resolver.shouldCleanupAfter()).thenReturn(Boolean.TRUE);
        when(resolver.shouldEvictCache()).thenReturn(Boolean.TRUE);
        final SecondLevelCacheStatement stmt = new SecondLevelCacheStatement(resolver, emfProducer, base);

        // WHEN
        stmt.evaluate();

        // THEN
        final InOrder order = inOrder(base, cache);
        verify(emfProducer).createEntityManagerFactory();
        order.verify(base).evaluate();
        order.verify(cache).evictAll();
        verify(emfProducer).destroyEntityManagerFactory(emf);
    }
}