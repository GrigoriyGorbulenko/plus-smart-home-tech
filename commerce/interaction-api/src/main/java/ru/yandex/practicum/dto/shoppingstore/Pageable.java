package ru.yandex.practicum.dto.shoppingstore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pageable {
    @Min(value = 0)
    @NotNull
    Integer page;
    @Min(value = 1)
    @NotNull
    Integer size;
    List<String> sort;
}
