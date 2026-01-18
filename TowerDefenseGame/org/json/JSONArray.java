package org.json;

import java.util.*;

public class JSONArray {
    private List<Object> list;
    
    public JSONArray() {
        this.list = new ArrayList<>();
    }
    
    public JSONArray(String source) {
        this();
        if (source == null || source.trim().isEmpty()) return;
        
        source = source.trim();
        if (!source.startsWith("[") || !source.endsWith("]")) {
            throw new RuntimeException("Invalid JSON Array");
        }
        
        parseArray(source.substring(1, source.length() - 1));
    }
    
    private void parseArray(String content) {
        if (content.trim().isEmpty()) return;
        
        List<String> tokens = tokenize(content);
        for (String token : tokens) {
            list.add(parseValue(token.trim()));
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
    
    public JSONArray put(Object value) {
        list.add(value);
        return this;
    }
    
    public Object get(int index) {
        return list.get(index);
    }
    
    public JSONObject getJSONObject(int index) {
        return (JSONObject) list.get(index);
    }
    
    public int length() {
        return list.size();
    }
    
    public String toString(int indent) {
        if (indent == 0) return toString();
        
        StringBuilder sb = new StringBuilder("[\n");
        
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            
            sb.append("  ".repeat(indent));
            
            if (value instanceof JSONObject) {
                sb.append(((JSONObject) value).toString(indent));
            } else if (value instanceof JSONArray) {
                sb.append(((JSONArray) value).toString(indent));
            } else if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            
            if (i < list.size() - 1) sb.append(",");
        }
        
        sb.append("]");
        return sb.toString();
    }
}





