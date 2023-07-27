package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.CalendarEntity;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T22:37:03+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 18.0.2 (Oracle Corporation)"
)
@Component
public class CalendarMapperImpl implements CalendarMapper {

    @Override
    public CalendarDto mapFromEntity(CalendarEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CalendarDto.CalendarDtoBuilder calendarDto = CalendarDto.builder();

        calendarDto.calendarId( entity.getCalendarId() );
        calendarDto.date( entity.getDate() );
        List<LocalTime> list = entity.getHours();
        if ( list != null ) {
            calendarDto.hours( new ArrayList<LocalTime>( list ) );
        }
        calendarDto.doctor( entity.getDoctor() );

        return calendarDto.build();
    }
}
