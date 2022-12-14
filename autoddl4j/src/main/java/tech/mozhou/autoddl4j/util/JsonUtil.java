package tech.mozhou.autoddl4j.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tech.mozhou.autoddl4j.exception.Autoddl4jParserException;

/**
 * Created by liuyuancheng on 2022/10/26  <br/>
 *
 * @author liuyuancheng
 */
public class JsonUtil {
    private static ObjectMapper objectMapper;

    static {
       objectMapper = new ObjectMapper();
       objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static String toJsonStr(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new Autoddl4jParserException(e);
        }
    }

    public static String toJsonPrettyStr(Object o){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new Autoddl4jParserException(e);
        }
    }
}
