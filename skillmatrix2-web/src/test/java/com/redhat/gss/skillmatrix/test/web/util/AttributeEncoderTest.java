package com.redhat.gss.skillmatrix.test.web.util;

import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.EmailFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/10/13
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttributeEncoderTest {

    @Test
    public void correctEncoding() throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("value", "korak");
        String encoded = AttributeEncoder.encodeFromMap("test", data);
        Map<String, String> decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertEquals("filter not encoded/decoded correctly", data.get("value"), decoded.get("value"));

        data = new HashMap<String, String>();
        data.put("value", "watc;hout!");
        encoded = AttributeEncoder.encodeFromMap("test", data);
        decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertEquals("char ; not encoded/decoded correctly", data.get("value"), decoded.get("value"));

        data = new HashMap<String, String>();
        data.put("value", "w\\atc;hou\\t!");
        encoded = AttributeEncoder.encodeFromMap("test", data);
        decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertEquals("chars \\; not encoded/decoded correctly", data.get("value"), decoded.get("value"));

        //add more
        data = new HashMap<String, String>();
        encoded = AttributeEncoder.encodeFromMap("test", data);
        decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertTrue("empty params not encoded/decoded correctly", decoded.isEmpty());

        data = new HashMap<String, String>();
        data.put("test", "36548");
        data.put("testminus", "-32145");
        encoded = AttributeEncoder.encodeFromMap("test", data);
        decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertEquals("number param filter not encoded/decoded correctly", data.get("test"), decoded.get("test"));
        assertEquals("number param filter not encoded/decoded correctly", data.get("testminus"), decoded.get("testminus"));

        data = new HashMap<String, String>();
        data.put("name", "korak");
        data.put("value", "watc;hout!");
        data.put("value2", "w\\atc;hou\\t!");
        encoded = AttributeEncoder.encodeFromMap("test", data);
        decoded = AttributeEncoder.decodeToMap(encoded, "test");
        assertEquals("multi-param filter not encoded/decoded correctly", data.get("value"), decoded.get("value"));
        assertEquals("multi-param filter not encoded/decoded correctly", data.get("value2"), decoded.get("value2"));
        assertEquals("multi-param filter not encoded/decoded correctly", data.get("name"), decoded.get("name"));


    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyType() throws Exception {
        String testString= ":param=value;";
        AttributeEncoder.decodeToMap(testString, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void incorrectFormat() throws Exception {
        String testString = "completely wrong f";
        AttributeEncoder.decodeToMap(testString, "test");
    }

    @Test(expected = TypeMismatchException.class)
    public void typeMismatch() throws Exception {
        String testString = "test2:value=param;";
        AttributeEncoder.decodeToMap(testString, "test");
    }


    @Test(expected = IllegalArgumentException.class)
    public void incorrectParams() throws Exception {
        String testString = "test:value=para;am;";
        AttributeEncoder.decodeToMap(testString, "test");
    }

}
