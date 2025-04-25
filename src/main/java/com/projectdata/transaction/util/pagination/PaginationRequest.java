package com.projectdata.transaction.util.pagination;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PaginationRequest {
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 10;
    
    @Builder.Default
    private List<SortRequest> sorts = new ArrayList<>();

    public Pageable toPageable() {
        if (sorts.isEmpty()) {
            return PageRequest.of(page, size);
        }

        List<Sort.Order> orders = sorts.stream()
                .map(sort -> new Sort.Order(
                    sort.getDirection(), 
                    sort.getField(), 
                    sort.getNullHandling()))
                .toList();

        return PageRequest.of(page, size, Sort.by(orders));
    }

    @Data
    @Builder
    public static class SortRequest {
        private String field;
        
        @Builder.Default
        private Sort.Direction direction = Sort.Direction.ASC;
        
        @Builder.Default
        private Sort.NullHandling nullHandling = Sort.NullHandling.NATIVE;
    }
}
