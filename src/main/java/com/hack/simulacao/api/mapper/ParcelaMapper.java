package com.hack.simulacao.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.hack.simulacao.api.dto.ParcelaResponse;
import com.hack.simulacao.domain.h2.Parcela;

@Mapper(componentModel = "spring")
public interface ParcelaMapper {
    ParcelaMapper INSTANCE = Mappers.getMapper(ParcelaMapper.class);

    ParcelaResponse toResponse(Parcela parcela);
    List<ParcelaResponse> toResponse(List<Parcela> parcelas);
}
