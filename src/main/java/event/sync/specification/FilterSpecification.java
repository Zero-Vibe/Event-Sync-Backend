package event.sync.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterSpecification {
    public static <T> Specification<T> parseSpecificationJson(String rawFilter) {
        return ((root, query, criteriaBuilder) -> {
            if (rawFilter == null ||  rawFilter.trim().isEmpty() || rawFilter.equals("{}")) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();

            try {
                Map<String, ?> filters = new ObjectMapper().readValue(rawFilter, Map.class);
                for (Map.Entry<String, ?> entry : filters.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) continue;
                    if (key.endsWith("_id")) {
                        String relatedObject = key.substring(0, key.length() - 3);
                        predicates.add(criteriaBuilder.equal(root.get(relatedObject).get("id"), value));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get(key), value));
                    }
                }
            } catch (Exception e) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
