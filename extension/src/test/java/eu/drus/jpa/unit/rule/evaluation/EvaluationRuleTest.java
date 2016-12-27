package eu.drus.jpa.unit.rule.evaluation;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.jpa.unit.core.metadata.FeatureResolver;
import eu.drus.jpa.unit.core.metadata.FeatureResolverFactory;
import eu.drus.jpa.unit.rule.evaluation.EvaluationRule;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationRuleTest {

    @Mock
    private FeatureResolverFactory featureResolverFactory;

    @Mock
    private FeatureResolver resolver;

    @Mock
    private Statement base;

    @Mock
    private FrameworkMethod method;

    @Before
    public void setUp() throws Exception {
        when(featureResolverFactory.createFeatureResolver(any(Method.class), any(Class.class))).thenReturn(resolver);
        when(resolver.getSeedData()).thenReturn(Collections.emptyList());
    }

    @Test
    public void testApplyRule() {
        // GIVEN
        final EvaluationRule rule = new EvaluationRule(featureResolverFactory, Collections.emptyMap());

        // WHEN
        final Statement stmt = rule.apply(base, method, this);

        // THEN
        assertThat(stmt, not(nullValue()));
    }
}