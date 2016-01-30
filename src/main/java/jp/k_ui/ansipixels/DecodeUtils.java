package jp.k_ui.ansipixels;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.BaseEncoding;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.InflaterOutputStream;

public class DecodeUtils {
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static ObjectReader LIST_LIST_INT_READER =
            OBJECT_MAPPER.readerFor(new TypeReference<List<List<Integer>>>() {
            });

    public static byte[] convertToBytesFromBase64(String base64) {
        return BaseEncoding.base64Url().decode(base64);
    }

    public static byte[] convertToBytesFromZippedBytes(byte[] zipped) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(new InflaterOutputStream(bytes));
        out.write(zipped);
        out.close();
        return bytes.toByteArray();
    }

    public static List<List<Integer>> parseCodeMatrix(String jsonString, String keyName)
            throws IOException {
        JsonNode rootNode = OBJECT_MAPPER.readTree(jsonString);
        if (rootNode.isArray()) {
            return LIST_LIST_INT_READER.readValue(rootNode);
        } else if (rootNode.isObject()) {
            JsonNode codes = rootNode.get(keyName);
            return LIST_LIST_INT_READER.readValue(codes);
        } else {
            throw new IOException("Unexpected JSON format: " + rootNode.getNodeType().toString());
        }
    }
}
