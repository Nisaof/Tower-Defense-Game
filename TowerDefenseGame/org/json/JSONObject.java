// Minimal JSON library for the project
package org.json;

import java.util.*;

public class JSONObject {
    private Map<String, Object> map;
    
    public JSONObject() {
        this.map = new LinkedHashMap<>();
    }
    
    public JSONObject(String source) {
        this();
        if (source == null || source.trim().isEmpty()) return;
        
        source = source.trim();
        if (!source.startsWith("{") || !source.endsWith("}")) {
            throw new RuntimeException("Invalid JSON");
        }
        
        parseObject(source.substring(1, source.length() - 1));
    }
    
    private void parseObject(String content) {
        if (content.trim().isEmpty()) return;
        
        List<String> tokens = tokenize(content);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.contains(":")) {
                String[] parts = token.split(":", 2);
                String key = parts[0].trim().replaceAll("^\"|\"$", "");
                String value = parts[1].trim();
                
                map.put(key, parseValue(value));
            }
        }
    }
    
    private List<String> tokenize(String content) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        boolean inString = false;
        
        for (char c : content.toCharArray()) {
            if (c == '"' && (current.length() == 0 || current.charAt(current.length() - 1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{' || c == '[') depth++;
                if (c == '}' || c == ']') depth--;
                
                if (c == ',' && depth == 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                    continue;
                }
            }
            
            current.append(c);
        }
        
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        
        return tokens;
    }
    
    private Object parseValue(String value) {
        value = value.trim();
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else if (value.equals("null")) {
            return null;
        } else if (value.startsWith("{")) {
            return new JSONObject(value);
        } else if (value.startsWith("[")) {
            return new JSONArray(value);
        } else {
            try {
                if (value.contains(".")) {
                    return Double.parseDouble(value);
                } else {
                    return Integer.parseInt(value);
                }
            } catch (NumberFormatException e) {
                return value;
            }
        }
    }
    
    public JSONObject put(String key, Object value) {
        map.put(key, value);
        return this;
    }
    
    public Object get(String key) {
        return map.get(key);
    }
    
    public String getString(String key) {
        return (String) map.get(key);
    }
    
    public int getInt(String key) {
        Object value = map.get(key);
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        return Integer.parseInt(value.toString());
    }
    
    public JSONObject getJSONObject(String key) {
        return (JSONObject) map.get(key);
    }
    
    public JSONArray getJSONArray(String key) {
        return (JSONArray) map.get(key);
    }
    
    public boolean has(String key) {
        return map.containsKey(key);
    }
    
    public Set<String> keySet() {
        return map.keySet();
    }
    
    public String toString(int indent) {
        if (indent == 0) return toString();
        
        StringBuilder sb = new StringBuilder("{\n");
        List<String> keys = new ArrayList<>(map.keySet());
        
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            
            sb.append("  ".repeat(indent)).append("\"").append(key).append("\": ");
            
            if (value instanceof JSONObject) {
                sb.append(((JSONObject) value).toString(indent));
            } else if (value instanceof JSONArray) {
                sb.append(((JSONArray) value).toString(indent));
            } else if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            
            if (i < keys.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        List<String> keys = new ArrayList<>(map.keySet());
        
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            
            sb.append("\"").append(key).append("\":");
            
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            
            if (i < keys.size() - 1) sb.append(",");
        }
        
        sb.append("}");
        return sb.toString();
    }
}





