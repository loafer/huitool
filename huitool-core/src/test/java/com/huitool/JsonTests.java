package com.huitool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * <描述信息>
 */
public class JsonTests {
    private ObjectMapper mapper;

    @Before
    public void setup(){
        mapper = new ObjectMapper();
    }

    @Test
    public void test() throws JsonProcessingException {
        Foo foo = new Foo(2.1234, new Date());
        System.out.println(mapper.writeValueAsString(foo));
    }

    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.OBJECT)
    static class Foo{
        @JsonFormat
        private Double value;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date creatTime;

        public Foo(Double value, Date creatTime) {
            this.value = value;
            this.creatTime = creatTime;
        }

        public Double getValue() {
            return value;
        }

        public Date getCreatTime() {
            return creatTime;
        }
    }
}
