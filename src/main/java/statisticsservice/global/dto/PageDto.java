package statisticsservice.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

    private List<T> content;

    private int totalPages;

    private long totalElements;

    private boolean first;

    private boolean last;

    private boolean sorted;

    private int size;

    private int pageNumber;

    private int numberOfElements;

    public PageDto(Page<T> page) {
        content = page.getContent();
        totalPages = page.getTotalPages();
        totalElements = page.getTotalElements();
        first = page.isFirst();
        last = page.isLast();
        sorted = page.getSort().isSorted();
        size = page.getSize();
        pageNumber = page.getNumber() + 1;
        numberOfElements = page.getNumberOfElements();
    }
}
