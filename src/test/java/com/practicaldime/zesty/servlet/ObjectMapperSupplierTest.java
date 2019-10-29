package com.practicaldime.zesty.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectMapperSupplierTest {

    private ObjectMapper mapper = ObjectMapperSupplier.version2.get();

    @Test
    public void testDateFormat() throws JsonProcessingException {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .optionalStart()
                .appendLiteral('T')
                .appendOptional(DateTimeFormatter.ISO_TIME)
                .toFormatter();
        System.out.println(formatter.toString());

        Date date = new Date();
        String dateStr = mapper.writeValueAsString(date);
        System.out.println(dateStr);
        assertTrue("Should match 'yyyy-MM-dd'T'hh:mm:ss.sss' pattern", Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.*").matcher(dateStr).find());
    }

    @Test
    public void testParseDate() throws IOException {
        String dateStr = "\"2019-09-08T06:50:07.007-0500\"";
        Date date = mapper.readValue(dateStr, Date.class);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals("Expected '9'", 9, cal.get(Calendar.MONTH) + 1);

        dateStr = "\"2019-07-29T03:02:06.000-0500\"";
        date = mapper.readValue(dateStr, Date.class);
        cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals("Expected '3'", 3, cal.get(Calendar.HOUR));
    }
}