package com.projectdata.transaction.util.pagination;

import com.projectdata.transaction.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationUtil {
    
    public static <T, R> PageResponse<R> mapPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public static PaginationRequest.SortRequest parseSortRequest(String field, String direction) {
        return PaginationRequest.SortRequest.builder()
                .field(field)
                .direction(Sort.Direction.fromString(direction.toUpperCase()))
                .build();
    }

    public static PaginationRequest buildPaginationRequest(int page, int size, String sortBy, String direction) {
        return PaginationRequest.builder()
                .page(page)
                .size(size)
                .sorts(List.of(parseSortRequest(sortBy, direction)))
                .build();
    }
}
