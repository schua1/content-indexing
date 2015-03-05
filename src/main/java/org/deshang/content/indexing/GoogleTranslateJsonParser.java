package org.deshang.content.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleTranslateJsonParser {

    private Pattern pattern = Pattern.compile("\\[[^\\[\\]]*\\]");

    public static void main(String[] args) {

        GoogleTranslateJsonParser parser = new GoogleTranslateJsonParser();
        List<Object> elements = parser.parse("[[[\"Respect\",\"尊重\"],[,,,\"Zūnzhòng\"]],[[\"verb\",[\"respect\",\"esteem\",\"value\"],[[\"respect\",[\"尊重\",\"关于\",\"遵守\",\"尊敬\",\"敬\",\"褒\"],[52719],0.30978554],[\"esteem\",[\"尊重\",\"敬重\",\"崇敬\",\"崇\",\"宝贵\",\"宝重\"],[52719]],[\"value\",[\"重视\",\"看重\",\"珍视\",\"宝贵\",\"鉴赏\",\"尊重\"],[52719]]],\"尊重\",2],[\"noun\",[\"homage\"],[[\"homage\",[\"尊敬\",\"尊重\"]]],\"尊重\",1]],\"zh-CN\",,,[[\"尊重\",1,[[\"Respect\",1000,true,false],[\"Respect for\",0,true,false],[\"Respected\",0,true,false],[\"Respect the\",0,true,false],[\"To respect\",0,true,false]],[[0,2]],\"尊重\",0,1]],0.0069698533,,[[\"zh-CN\"],,[0.0069698533]],,,,,,[[\"重\",\"尊\",\"不尊重\"]]]");
        parser.printList(elements, 0);
    }

    public List<Object> parse(String jsonString) {
        Map<String, Object> elements = new HashMap<String, Object>();
        List<Object> elementList = new ArrayList<Object>();
        parse(jsonString, elementList, elements);
        return elementList;
    }

    @SuppressWarnings("unchecked")
    private void parse(String jsonString, List<Object> elementList, Map<String, Object> elements) {

        List<Object> subElementList = new ArrayList<Object>();
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
            String elementString = jsonString.substring(matcher.start() + 1, matcher.end() - 1);
            String[] elementArray = elementString.replaceAll("\"", "").split(",");
            subElementList.addAll(Arrays.asList(elementArray));

            for (int i = 0; i < subElementList.size(); i++) {
                Object obj = subElementList.get(i);
                if (obj!= null && elements.get(obj) != null) {
                    subElementList.set(i, elements.get(obj));
                }
            }
            
            String elementIdx = elementString.replaceAll(",", "|").replaceAll("\"", "");
            elements.put(elementIdx, subElementList);
            String newJsonString = jsonString.replace("[" + elementString + "]", elementIdx);
            parse(newJsonString, elementList, elements);
        } else {
            elementList.addAll(((List<Object>)elements.get(jsonString)));
        }
    }

    @SuppressWarnings("unchecked")
    private void printList(List<Object> elements, int level) {
        for (Object element : elements) {
            if (element instanceof List) {
                printList(((List<Object>) element), level + 1);
            } else {
                String indent = new String(new char[level]).replace("\0", "  ");
                System.out.println(indent + "Level " + level + " element: " + element);
            }
        }
        
    }
}
