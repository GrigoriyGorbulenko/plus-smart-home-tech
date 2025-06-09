package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;

@UtilityClass
public class AddressMapper {

    public Address mapToAddress(AddressDto dto) {
        return Address.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }

    public AddressDto mapToAddressDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }
}
