package com.demo.api.rest.config.jersey;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom configuration of validation. This configuration defines custom:
 * <ul>
 *     <li>ConstraintValidationFactory - so that validators are able to inject Jersey providers/resources.</li>
 *     <li>ParameterNameProvider - if method input parameters are invalid, this class returns actual parameter names
 *     instead of the default ones ({@code arg0, arg1, ..})</li>
 * </ul>
 */
@Provider
public class ValidationContextConfig implements ContextResolver<org.glassfish.jersey.server.validation.ValidationConfig> {
    
    /**
     * Get context method.
     *
     * @param type	Type of class.
     * @return	Validation config.
     */
    @Override
    public org.glassfish.jersey.server.validation.ValidationConfig getContext(final Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.parameterNameProvider(new RestAnnotationParameterNameProvider());

        return config;
    }

    private static class RestAnnotationParameterNameProvider extends DefaultParameterNameProvider {
        @Override
        public List<String> getParameterNames(Method method) {
            Annotation[][] annotationsByParam = method.getParameterAnnotations();
            List<String> names = new ArrayList<String>(annotationsByParam.length);

            for (Annotation[] annotations : annotationsByParam) {
                String name = getParamName(annotations);
                if (name == null) {
                    name = "arg" + (names.size() + 1);
                }
                names.add(name);
            }

            return names;
        }

        private static String getParamName(Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == QueryParam.class) {
                    return QueryParam.class.cast(annotation).value();
                } else if (annotation.annotationType() == PathParam.class) {
                    return PathParam.class.cast(annotation).value();
                }
            }

            return null;
        }
    }
}