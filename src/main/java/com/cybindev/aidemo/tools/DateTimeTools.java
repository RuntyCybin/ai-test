package com.cybindev.aidemo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Herramientas que el modelo puede invocar: fecha/hora actual. */
@Component
public class DateTimeTools {

    @Tool(description = "Devuelve la fecha y hora actual en la zona horaria indicada")
    public String currentDateTime(
            @ToolParam(description = "Zona horaria IANA, p. ej. Europe/Madrid o America/Mexico_City") String timeZone) {
        return ZonedDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
