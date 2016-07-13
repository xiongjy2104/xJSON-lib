package com.cmbchina.ccd.pluto.logistracker.util;
//package net.sf.json.xml;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import net.sf.json.*;
import net.sf.json.util.JSONUtils;
import nu.xom.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class XMLSerializer {
    private static final String[] EMPTY_ARRAY = new String[0];
    private static final String JSON_PREFIX = "json_";
    private static final Log log= LogFactory.getLog(XMLSerializer.class);;
    private String arrayName;
    private String elementName;
    private String[] expandableProperties;
    private boolean forceTopLevelObject;
    private boolean namespaceLenient;
    private Map namespacesPerElement = new TreeMap();
    private String objectName;
    private boolean removeNamespacePrefixFromElements;
    private String rootName;
    private Map rootNamespace = new TreeMap();
    private boolean skipNamespaces;
    private boolean skipWhitespace;
    private boolean trimSpaces;
    private boolean typeHintsCompatibility;
    private boolean typeHintsEnabled;

    public XMLSerializer() {
        this.setObjectName("o");
        this.setArrayName("a");
        this.setElementName("e");
        this.setTypeHintsEnabled(true);
        this.setTypeHintsCompatibility(true);
        this.setNamespaceLenient(false);
        this.setSkipNamespaces(false);
        this.setRemoveNamespacePrefixFromElements(false);
        this.setTrimSpaces(false);
        this.setExpandableProperties(EMPTY_ARRAY);
        this.setSkipNamespaces(false);
    }

    public void addNamespace(String prefix, String uri) {
        this.addNamespace(prefix, uri, (String) null);
    }

    public void addNamespace(String prefix, String uri, String elementName) {
        if (!StringUtils.isBlank(uri)) {
            if (prefix == null) {
                prefix = "";
            }

            if (StringUtils.isBlank(elementName)) {
                this.rootNamespace.put(prefix.trim(), uri.trim());
            } else {
                Object nameSpaces = (Map) this.namespacesPerElement.get(elementName);
                if (nameSpaces == null) {
                    nameSpaces = new TreeMap();
                    this.namespacesPerElement.put(elementName, nameSpaces);
                }

                ((Map) nameSpaces).put(prefix, uri);
            }

        }
    }

    public void clearNamespaces() {
        this.rootNamespace.clear();
        this.namespacesPerElement.clear();
    }

    public void clearNamespaces(String elementName) {
        if (StringUtils.isBlank(elementName)) {
            this.rootNamespace.clear();
        } else {
            this.namespacesPerElement.remove(elementName);
        }

    }

    public String getArrayName() {
        return this.arrayName;
    }

    public String getElementName() {
        return this.elementName;
    }

    public String[] getExpandableProperties() {
        return this.expandableProperties;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public String getRootName() {
        return this.rootName;
    }

    public boolean isForceTopLevelObject() {
        return this.forceTopLevelObject;
    }

    public boolean isNamespaceLenient() {
        return this.namespaceLenient;
    }

    public boolean isRemoveNamespacePrefixFromElements() {
        return this.removeNamespacePrefixFromElements;
    }

    public boolean isSkipNamespaces() {
        return this.skipNamespaces;
    }

    public boolean isSkipWhitespace() {
        return this.skipWhitespace;
    }

    public boolean isTrimSpaces() {
        return this.trimSpaces;
    }

    public boolean isTypeHintsCompatibility() {
        return this.typeHintsCompatibility;
    }

    public boolean isTypeHintsEnabled() {
        return this.typeHintsEnabled;
    }

    public JSON read(String xml) {
        Object json = null;

        try {
            Document e = (new Builder()).build(new StringReader(xml));
            Element root = e.getRootElement();
            if (this.isNullObject(root)) {
                return JSONNull.getInstance();
            } else {
                String defaultType = this.getType(root, "string");
                String key;
                if (this.isArray(root, true)) {
                    json = this.processArrayElement(root, defaultType);
                    if (this.forceTopLevelObject) {
                        key = this.removeNamespacePrefix(root.getQualifiedName());
                        json = (new JSONObject()).element(key, json);
                    }
                } else {
                    json = this.processObjectElement(root, defaultType);
                    if (this.forceTopLevelObject) {
                        key = this.removeNamespacePrefix(root.getQualifiedName());
                        json = (new JSONObject()).element(key, json);
                    }
                }

                return (JSON) json;
            }
        } catch (JSONException var7) {
            throw var7;
        } catch (Exception var8) {
            throw new JSONException(var8);
        }
    }

    public JSON readFromFile(File file) {
        if (file == null) {
            throw new JSONException("File is null");
        } else if (!file.canRead()) {
            throw new JSONException("Can\'t read input file");
        } else if (file.isDirectory()) {
            throw new JSONException("File is a directory");
        } else {
            try {
                return this.readFromStream(new FileInputStream(file));
            } catch (IOException var3) {
                throw new JSONException(var3);
            }
        }
    }

    public JSON readFromFile(String path) {
        return this.readFromStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
    }

    public JSON readFromStream(InputStream stream) {
        try {
            StringBuffer ioe = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line = null;

            while ((line = in.readLine()) != null) {
                ioe.append(line);
            }

            return this.read(ioe.toString());
        } catch (IOException var5) {
            throw new JSONException(var5);
        }
    }

    public void removeNamespace(String prefix) {
        this.removeNamespace(prefix, (String) null);
    }

    public void removeNamespace(String prefix, String elementName) {
        if (prefix == null) {
            prefix = "";
        }

        if (StringUtils.isBlank(elementName)) {
            this.rootNamespace.remove(prefix.trim());
        } else {
            Map nameSpaces = (Map) this.namespacesPerElement.get(elementName);
            nameSpaces.remove(prefix);
        }

    }

    public void setArrayName(String arrayName) {
        this.arrayName = StringUtils.isBlank(arrayName) ? "a" : arrayName;
    }

    public void setElementName(String elementName) {
        this.elementName = StringUtils.isBlank(elementName) ? "e" : elementName;
    }

    public void setExpandableProperties(String[] expandableProperties) {
        this.expandableProperties = expandableProperties == null ? EMPTY_ARRAY : expandableProperties;
    }

    public void setForceTopLevelObject(boolean forceTopLevelObject) {
        this.forceTopLevelObject = forceTopLevelObject;
    }

    public void setNamespace(String prefix, String uri) {
        this.setNamespace(prefix, uri, (String) null);
    }

    public void setNamespace(String prefix, String uri, String elementName) {
        if (!StringUtils.isBlank(uri)) {
            if (prefix == null) {
                prefix = "";
            }

            if (StringUtils.isBlank(elementName)) {
                this.rootNamespace.clear();
                this.rootNamespace.put(prefix.trim(), uri.trim());
            } else {
                Object nameSpaces = (Map) this.namespacesPerElement.get(elementName);
                if (nameSpaces == null) {
                    nameSpaces = new TreeMap();
                    this.namespacesPerElement.put(elementName, nameSpaces);
                }

                ((Map) nameSpaces).clear();
                ((Map) nameSpaces).put(prefix, uri);
            }

        }
    }

    public void setNamespaceLenient(boolean namespaceLenient) {
        this.namespaceLenient = namespaceLenient;
    }

    public void setObjectName(String objectName) {
        this.objectName = StringUtils.isBlank(objectName) ? "o" : objectName;
    }

    public void setRemoveNamespacePrefixFromElements(boolean removeNamespacePrefixFromElements) {
        this.removeNamespacePrefixFromElements = removeNamespacePrefixFromElements;
    }

    public void setRootName(String rootName) {
        this.rootName = StringUtils.isBlank(rootName) ? null : rootName;
    }

    public void setSkipNamespaces(boolean skipNamespaces) {
        this.skipNamespaces = skipNamespaces;
    }

    public void setSkipWhitespace(boolean skipWhitespace) {
        this.skipWhitespace = skipWhitespace;
    }

    public void setTrimSpaces(boolean trimSpaces) {
        this.trimSpaces = trimSpaces;
    }

    public void setTypeHintsCompatibility(boolean typeHintsCompatibility) {
        this.typeHintsCompatibility = typeHintsCompatibility;
    }

    public void setTypeHintsEnabled(boolean typeHintsEnabled) {
        this.typeHintsEnabled = typeHintsEnabled;
    }

    public String write(JSON json) {
        return this.write(json, (String) null);
    }

    public String write(JSON json, String encoding) {
        JSONObject jsonObject;
        if (JSONNull.getInstance().equals(json)) {
            jsonObject = null;
            Element jsonObject2 = this.newElement(this.getRootName() == null ? this.getObjectName() : this.getRootName());
            jsonObject2.addAttribute(new Attribute(this.addJsonPrefix("null"), "true"));
            Document root1 = new Document(jsonObject2);
            return this.writeDocument(root1, encoding);
        } else {
            Element root;
            Document doc;
            if (json instanceof JSONArray) {
                JSONArray jsonObject1 = (JSONArray) json;
                root = this.processJSONArray(jsonObject1, this.newElement(this.getRootName() == null ? this.getArrayName() : this.getRootName()), this.expandableProperties);
                doc = new Document(root);
                return this.writeDocument(doc, encoding);
            } else {
                jsonObject = (JSONObject) json;
                root = null;
                if (jsonObject.isNullObject()) {
                    root = this.newElement(this.getObjectName());
                root.addAttribute(new Attribute(this.addJsonPrefix("null"), "true"));
            } else {
                    if((jsonObject.names().size()==1)){
                        String elementName=  jsonObject.names().getString(0);
                        Object obj=jsonObject.getJSONObject(jsonObject.names().getString(0));
                        if(obj instanceof JSONObject)
                            root = this.processJSONObject(jsonObject.getJSONObject(jsonObject.names().getString(0)),this.newElement(elementName), this.expandableProperties, true);
                        else
                            root = this.processJSONValue(obj,this.newElement(elementName),this.newElement(elementName), this.expandableProperties);
                    }else{
                        String elementName= (jsonObject.names().size()==1)? jsonObject.names().getString(0) :(this.getRootName() == null ? this.getObjectName() : this.getRootName());
                        root = this.processJSONObject(jsonObject,this.newElement(elementName), this.expandableProperties, true);
                    }
            }

            doc = new Document(root);
            return this.writeDocument(doc, encoding);
            }
        }
    }

    private String addJsonPrefix(String str) {
        return !this.isTypeHintsCompatibility() ? "json_" + str : str;
    }

    private void addNameSpaceToElement(Element element) {
        String elementName = null;
        if (element instanceof XMLSerializer.CustomElement) {
            elementName = ((XMLSerializer.CustomElement) element).getQName();
        } else {
            elementName = element.getQualifiedName();
        }

        Map nameSpaces = (Map) this.namespacesPerElement.get(elementName);
        if (nameSpaces != null && !nameSpaces.isEmpty()) {
            this.setNamespaceLenient(true);
            Iterator entries = nameSpaces.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String prefix = (String) entry.getKey();
                String uri = (String) entry.getValue();
                if (StringUtils.isBlank(prefix)) {
                    element.setNamespaceURI(uri);
                } else {
                    element.addNamespaceDeclaration(prefix, uri);
                }
            }
        }

    }

    private boolean checkChildElements(Element element, boolean isTopLevel) {
        int childCount = element.getChildCount();
        Elements elements = element.getChildElements();
        int elementCount = elements.size();
        if (childCount == 1 && element.getChild(0) instanceof Text) {
            return isTopLevel;
        } else {
            if (childCount == elementCount) {
                if (elementCount == 0) {
                    return true;
                }

                if (elementCount == 1) {
                    if (!this.skipWhitespace && !(element.getChild(0) instanceof Text)) {
                        return false;
                    }

                    return true;
                }
            }

            if (childCount > elementCount) {
                for (int childName = 0; childName < childCount; ++childName) {
                    Node i = element.getChild(childName);
                    if (i instanceof Text) {
                        Text text = (Text) i;
                        if (StringUtils.isNotBlank(StringUtils.strip(text.getValue())) && !this.skipWhitespace) {
                            return false;
                        }
                    }
                }
            }

            String var9 = elements.get(0).getQualifiedName();

            for (int var10 = 1; var10 < elementCount; ++var10) {
                if (var9.compareTo(elements.get(var10).getQualifiedName()) != 0) {
                    return false;
                }
            }

            return true;
        }
    }

    private String getClass(Element element) {
        Attribute attribute = element.getAttribute(this.addJsonPrefix("class"));
        String clazz = null;
        if (attribute != null) {
            String clazzText = attribute.getValue().trim();
            if ("object".compareToIgnoreCase(clazzText) == 0) {
                clazz = "object";
            } else if ("array".compareToIgnoreCase(clazzText) == 0) {
                clazz = "array";
            }
        }

        return clazz;
    }

    private String getType(Element element) {
        return this.getType(element, (String) null);
    }

    private String getType(Element element, String defaultType) {
        Attribute attribute = element.getAttribute(this.addJsonPrefix("type"));
        String type = null;
        if (attribute != null) {
            String typeText = attribute.getValue().trim();
            if ("boolean".compareToIgnoreCase(typeText) == 0) {
                type = "boolean";
            } else if ("number".compareToIgnoreCase(typeText) == 0) {
                type = "number";
            } else if ("integer".compareToIgnoreCase(typeText) == 0) {
                type = "integer";
            } else if ("float".compareToIgnoreCase(typeText) == 0) {
                type = "float";
            } else if ("object".compareToIgnoreCase(typeText) == 0) {
                type = "object";
            } else if ("array".compareToIgnoreCase(typeText) == 0) {
                type = "array";
            } else if ("string".compareToIgnoreCase(typeText) == 0) {
                type = "string";
            } else if ("function".compareToIgnoreCase(typeText) == 0) {
                type = "function";
            }
        } else if (defaultType != null) {
            log.info("Using default type " + defaultType);
            type = defaultType;
        }

        return type;
    }

    private boolean hasNamespaces(Element element) {
        int namespaces = 0;

        for (int i = 0; i < element.getNamespaceDeclarationCount(); ++i) {
            String prefix = element.getNamespacePrefix(i);
            String uri = element.getNamespaceURI(prefix);
            if (!StringUtils.isBlank(uri)) {
                ++namespaces;
            }
        }

        return namespaces > 0;
    }

    private boolean isArray(Element element, boolean isTopLevel) {
        boolean isArray = false;
        String clazz = this.getClass(element);
        if (clazz != null && clazz.equals("array")) {
            isArray = true;
        } else if (element.getAttributeCount() == 0) {
            isArray = this.checkChildElements(element, isTopLevel);
        } else if (element.getAttributeCount() != 1 || element.getAttribute(this.addJsonPrefix("class")) == null && element.getAttribute(this.addJsonPrefix("type")) == null) {
            if (element.getAttributeCount() == 2 && element.getAttribute(this.addJsonPrefix("class")) != null && element.getAttribute(this.addJsonPrefix("type")) != null) {
                isArray = this.checkChildElements(element, isTopLevel);
            }
        } else {
            isArray = this.checkChildElements(element, isTopLevel);
        }

        if (isArray) {
            for (int j = 0; j < element.getNamespaceDeclarationCount(); ++j) {
                String prefix = element.getNamespacePrefix(j);
                String uri = element.getNamespaceURI(prefix);
                if (!StringUtils.isBlank(uri)) {
                    return false;
                }
            }
        }

        return isArray;
    }

    private boolean isFunction(Element element) {
        int attrCount = element.getAttributeCount();
        if (attrCount > 0) {
            Attribute typeAttr = element.getAttribute(this.addJsonPrefix("type"));
            Attribute paramsAttr = element.getAttribute(this.addJsonPrefix("params"));
            if (attrCount == 1 && paramsAttr != null) {
                return true;
            }

            if (attrCount == 2 && paramsAttr != null && typeAttr != null && (typeAttr.getValue().compareToIgnoreCase("string") == 0 || typeAttr.getValue().compareToIgnoreCase("function") == 0)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNullObject(Element element) {
        if (element.getChildCount() == 0) {
            if (element.getAttributeCount() == 0) {
                return true;
            }

            if (element.getAttribute(this.addJsonPrefix("null")) != null) {
                return true;
            }

            if (element.getAttributeCount() == 1 && (element.getAttribute(this.addJsonPrefix("class")) != null || element.getAttribute(this.addJsonPrefix("type")) != null)) {
                return true;
            }

            if (element.getAttributeCount() == 2 && element.getAttribute(this.addJsonPrefix("class")) != null && element.getAttribute(this.addJsonPrefix("type")) != null) {
                return true;
            }
        }

        if (this.skipWhitespace && element.getChildCount() == 1 && element.getChild(0) instanceof Text) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isObject(Element element, boolean isTopLevel) {
        boolean isObject = false;
        if (!this.isArray(element, isTopLevel) && !this.isFunction(element)) {
            if (this.hasNamespaces(element)) {
                return true;
            }

            int attributeCount = element.getAttributeCount();
            int childCount;
            if (attributeCount > 0) {
                childCount = element.getAttribute(this.addJsonPrefix("null")) == null ? 0 : 1;
                childCount += element.getAttribute(this.addJsonPrefix("class")) == null ? 0 : 1;
                childCount += element.getAttribute(this.addJsonPrefix("type")) == null ? 0 : 1;
                switch (attributeCount) {
                    case 1:
                        if (childCount == 0) {
                            return true;
                        }
                        break;
                    case 2:
                        if (childCount < 2) {
                            return true;
                        }
                        break;
                    case 3:
                        if (childCount < 3) {
                            return true;
                        }
                        break;
                    default:
                        return true;
                }
            }

            childCount = element.getChildCount();
            if (childCount == 1 && element.getChild(0) instanceof Text) {
                return isTopLevel;
            }

            isObject = true;
        }

        return isObject;
    }

    private Element newElement(String name) {
        if (name.indexOf(58) != -1) {
            this.namespaceLenient = true;
        }

        return (Element) (this.namespaceLenient ? new XMLSerializer.CustomElement(name) : new Element(name));
    }

    private JSON processArrayElement(Element element, String defaultType) {
        JSONArray jsonArray = new JSONArray();
        int childCount = element.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            Node child = element.getChild(i);
            if (child instanceof Text) {
                Text text = (Text) child;
                if (StringUtils.isNotBlank(StringUtils.strip(text.getValue()))) {
                    jsonArray.element(text.getValue());
                }
            } else if (child instanceof Element) {
                this.setValue(jsonArray, (Element) child, defaultType);
            }
        }

        return jsonArray;
    }

    private Object processElement(Element element, String type) {
        return this.isNullObject(element) ? JSONNull.getInstance() : (this.isArray(element, false) ? this.processArrayElement(element, type) : (this.isObject(element, false) ? this.processObjectElement(element, type) : this.trimSpaceFromValue(element.getValue())));
    }

    private Element processJSONArray(JSONArray array, Element root, String[] expandableProperties) {
        int l = array.size();

        for (int i = 0; i < l; ++i) {
            Object value = array.get(i);
            Element subElement=new Element(root.getLocalName());//(Element) null;//(Element) root.getParent()
            Element element = this.processJSONValue(value, root,subElement , expandableProperties);
            root.appendChild(element);
        }

        return root;
    }

    private Element processJSONObject(JSONObject jsonObject, Element root, String[] expandableProperties, boolean isRoot) {
        if (jsonObject.isNullObject()) {
            root.addAttribute(new Attribute(this.addJsonPrefix("null"), "true"));
            return root;
        } else if (jsonObject.isEmpty()) {
            return root;
        } else {
            Map.Entry element;
            String name;
            if (isRoot && !this.rootNamespace.isEmpty()) {
                this.setNamespaceLenient(true);
                Iterator names = this.rootNamespace.entrySet().iterator();

                while (names.hasNext()) {
                    element = (Map.Entry) names.next();
                    String i = (String) element.getKey();
                    name = (String) element.getValue();
                    if (StringUtils.isBlank(i)) {
                        root.setNamespaceURI(name);
                    } else {
                        root.addNamespaceDeclaration(i, name);
                    }
                }
            }

            this.addNameSpaceToElement(root);
            Object[] var14 = jsonObject.names().toArray();
            Arrays.sort(var14);
            element = null;

            for (int var16 = 0; var16 < var14.length; ++var16) {
                name = (String) var14[var16];
                Object value = jsonObject.get(name);
                if (name.startsWith("@xmlns")) {
                    this.setNamespaceLenient(true);
                    int var17 = name.indexOf(58);
                    if (var17 == -1) {
                        if (StringUtils.isBlank(root.getNamespaceURI())) {
                            root.setNamespaceURI(String.valueOf(value));
                        }
                    } else {
                        String var18 = name.substring(var17 + 1);
                        if (StringUtils.isBlank(root.getNamespaceURI(var18))) {
                            root.addNamespaceDeclaration(var18, String.valueOf(value));
                        }
                    }
                }
                else if (name.startsWith("@")) {
                    int idx = name.indexOf(':');
                    if (idx == -1) {
                        root.addAttribute(new Attribute(name.substring(1), String.valueOf(value)));
                    }else{
                        String var18 = name.substring(idx + 1);
                        root.addAttribute(new Attribute(var18, String.valueOf(value)));
                    }
                } else if (name.equals("#text")) {
                    if (value instanceof JSONArray) {
                        root.appendChild(((JSONArray) value).join("", true));
                    } else {
                        root.appendChild(String.valueOf(value));
                    }
                } else {
                    Element var15;
                    if (value instanceof JSONArray) {// && (((JSONArray) value).isExpandElements() || ArrayUtils.contains(expandableProperties, name))
                        JSONArray array = (JSONArray) value;
                        int l = array.size();

                        for (int j = 0; j < l; ++j) {
                            Object item = array.get(j);
                            var15 = this.newElement(name);
                            if (item instanceof JSONObject) {
                                var15 = this.processJSONValue((JSONObject) item, root, var15, expandableProperties);
                            } else if (item instanceof JSONArray) {
                                var15 = this.processJSONValue((JSONArray) item, root, var15, expandableProperties);
                            } else {
                                var15 = this.processJSONValue(item, root, var15, expandableProperties);
                            }

                            this.addNameSpaceToElement(var15);
                            root.appendChild(var15);
                        }
                    } else {
                        var15 = this.newElement(name);
                        var15 = this.processJSONValue(value, root, var15, expandableProperties);
                        this.addNameSpaceToElement(var15);
                        root.appendChild(var15);
                    }
                }
            }

            return root;
        }
    }

    private Element processJSONValue(Object value, Element root, Element target, String[] expandableProperties) {
        if (target == null) {
            target = this.newElement(this.getElementName());
        }

        if (JSONUtils.isBoolean(value)) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("type"), "boolean"));
            }

            target.appendChild(value.toString());
        } else if (JSONUtils.isNumber(value)) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("type"), "number"));
            }

            target.appendChild(value.toString());
        } else if (JSONUtils.isFunction(value)) {
            if (value instanceof String) {
                value = JSONFunction.parse((String) value);
            }

            JSONFunction func = (JSONFunction) value;
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("type"), "function"));
            }

            String params = ArrayUtils.toString(func.getParams());
            params = params.substring(1);
            params = params.substring(0, params.length() - 1);
            target.addAttribute(new Attribute(this.addJsonPrefix("params"), params));
            target.appendChild(new Text("<![CDATA[" + func.getText() + "]]>"));
        } else if (JSONUtils.isString(value)) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("type"), "string"));
            }

            target.appendChild(value.toString());
        } else if (value instanceof JSONArray) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("class"), "array"));
            }

            target = this.processJSONArray((JSONArray) value, target, expandableProperties);
        } else if (value instanceof JSONObject) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("class"), "object"));
            }

            target = this.processJSONObject((JSONObject) value, target, expandableProperties, false);
        } else if (JSONUtils.isNull(value)) {
            if (this.isTypeHintsEnabled()) {
                target.addAttribute(new Attribute(this.addJsonPrefix("class"), "object"));
            }

            target.addAttribute(new Attribute(this.addJsonPrefix("null"), "true"));
        }

        return target;
    }

    private JSON processObjectElement(Element element, String defaultType) {
        if (this.isNullObject(element)) {
            return JSONNull.getInstance();
        } else {

            JSONObject jsonObject = new JSONObject();
            int attrCount;
            if (!this.skipNamespaces) {
                for (attrCount = 0; attrCount < element.getNamespaceDeclarationCount(); ++attrCount) {
                    String childCount = element.getNamespacePrefix(attrCount);
                    String i = element.getNamespaceURI(childCount);
                    if (!StringUtils.isBlank(i)) {
                        if (!StringUtils.isBlank(childCount)) {
                            childCount = ":" + childCount;
                        }

                        this.setOrAccumulate(jsonObject, "@xmlns" + childCount, this.trimSpaceFromValue(i));
                    }
                }
            }

            attrCount = element.getAttributeCount();

            int var9;
            for (var9 = 0; var9 < attrCount; ++var9) {
                Attribute var10 = element.getAttribute(var9);
                String child = var10.getQualifiedName();
                if (!this.isTypeHintsEnabled() || this.addJsonPrefix("class").compareToIgnoreCase(child) != 0 && this.addJsonPrefix("type").compareToIgnoreCase(child) != 0) {
                    String text = var10.getValue();
                    this.setOrAccumulate(jsonObject, "@" + this.removeNamespacePrefix(child), this.trimSpaceFromValue(text));
                }
            }

            var9 = element.getChildCount();

            for (int var11 = 0; var11 < var9; ++var11) {
                Node var12 = element.getChild(var11);


                if (var12 instanceof Text) {
                    Text var13 = (Text) var12;
                    if (StringUtils.isNotBlank(StringUtils.strip(var13.getValue()))) {
//                        this.setOrAccumulate(jsonObject, "#text", this.trimSpaceFromValue(var13.getValue()));
                    }
                }
                else if (var12 instanceof Element) {
//                    this.setValue(jsonObject, (Element) var12, defaultType);
                    if(((Element) var12).getChildCount()<1) {
                        this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), "");//TO DO, null attribute return "" now.
                    } else if(((Element) var12).getChildCount()==1 && var12.getChild(0) instanceof Text) {
                        this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), this.trimSpaceFromValue(var12.getValue()));
                    } else{
                        JSONObject subJson=  processSubJSONObject((Element) var12);
                        this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), subJson);
//                    this.setValue(jsonObject, (Element) var12, defaultType);
                    }
                }
            }

            JSONObject jsonObjectKey = new JSONObject();
            jsonObjectKey.put(element.getLocalName(),jsonObject);
            return jsonObjectKey;
        }
    }


    private JSONObject processSubJSONObject(Element element) {
         JSONObject jsonObject=new JSONObject();
        int attrCount;
        if (!this.skipNamespaces) {
            for (attrCount = 0; attrCount < element.getNamespaceDeclarationCount(); ++attrCount) {
                String childCount = element.getNamespacePrefix(attrCount);
                String i = element.getNamespaceURI(childCount);
                if (!StringUtils.isBlank(i)) {
                    if (!StringUtils.isBlank(childCount)) {
                        childCount = ":" + childCount;
                    }

                    this.setOrAccumulate(jsonObject, "@xmlns" + childCount, this.trimSpaceFromValue(i));
                }
            }
        }

        attrCount = element.getAttributeCount();

        int var9;
        for (var9 = 0; var9 < attrCount; ++var9) {
            Attribute var10 = element.getAttribute(var9);
            String child = var10.getQualifiedName();
            if (!this.isTypeHintsEnabled() || this.addJsonPrefix("class").compareToIgnoreCase(child) != 0 && this.addJsonPrefix("type").compareToIgnoreCase(child) != 0) {
                String text = var10.getValue();
                this.setOrAccumulate(jsonObject, "@" + this.removeNamespacePrefix(child), this.trimSpaceFromValue(text));
            }
        }
        int childCount = element.getChildCount();

        for (int childIdx = 0; childIdx < childCount; ++childIdx) {
            Node var12 = element.getChild(childIdx);
            if(((Element) var12).getChildCount()<1) {
//                this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), "");//TO DO, null attribute return "" now.
            }
            if(((Element) var12).getChildCount()==1 && var12.getChild(0) instanceof Text)
                this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), this.trimSpaceFromValue(var12.getValue()));
            else{
                JSONObject subJson= (JSONObject) processSubJSONObject((Element) var12);
                this.setOrAccumulate(jsonObject, ((Element) var12).getLocalName(), subJson);
//                    this.setValue(jsonObject, (Element) var12, defaultType);
            }

//                if (var12 instanceof Text) {
//                    Text var13 = (Text) var12;
//                    if (StringUtils.isNotBlank(StringUtils.strip(var13.getValue()))) {
//                        this.setOrAccumulate(jsonObject, "#text", this.trimSpaceFromValue(var13.getValue()));
//                    }
//                }
// else if (var12 instanceof Element) {
//                    this.setValue(jsonObject, (Element) var12, defaultType);
//                }
        }
        return jsonObject;
    }

    private String removeNamespacePrefix(String name) {
        if (this.isRemoveNamespacePrefixFromElements()) {
            int colon = name.indexOf(58);
            return colon != -1 ? name.substring(colon + 1) : name;
        } else {
            return name;
        }
    }

    private void setOrAccumulate(JSONObject jsonObject, String key, Object value) {
        if (jsonObject.has(key)) {
            jsonObject.accumulate(key, value);
            Object val = jsonObject.get(key);
            if (val instanceof JSONArray) {
                ((JSONArray) val).setExpandElements(true);
            }
        } else {
            jsonObject.element(key, value);
        }

    }

    private void setValue(JSONArray jsonArray, Element element, String defaultType) {
        String clazz = this.getClass(element);
        String type = this.getType(element);
        type = type == null ? defaultType : type;
        if (this.hasNamespaces(element) && !this.skipNamespaces) {
            jsonArray.element(this.simplifyValue((JSONObject) null, this.processElement(element, type)));
        } else {
            String[] paramsAttribute;
            String params;
            if (element.getAttributeCount() > 0) {
                if (this.isFunction(element)) {
                    Attribute classProcessed1 = element.getAttribute(this.addJsonPrefix("params"));
                    paramsAttribute = null;
                    params = element.getValue();
                    paramsAttribute = StringUtils.split(classProcessed1.getValue(), ",");
                    jsonArray.element(new JSONFunction(paramsAttribute, params));
                } else {
                    jsonArray.element(this.simplifyValue((JSONObject) null, this.processElement(element, type)));
                }
            } else {
                boolean classProcessed = false;
                if (clazz != null) {
                    if (clazz.compareToIgnoreCase("array") == 0) {
                        jsonArray.element(this.processArrayElement(element, type));
                        classProcessed = true;
                    } else if (clazz.compareToIgnoreCase("object") == 0) {
                        jsonArray.element(this.simplifyValue((JSONObject) null, this.processObjectElement(element, type)));
                        classProcessed = true;
                    }
                }

                if (!classProcessed) {
                    if (type.compareToIgnoreCase("boolean") == 0) {
                        jsonArray.element(Boolean.valueOf(element.getValue()));
                    } else if (type.compareToIgnoreCase("number") == 0) {
                        try {
                            jsonArray.element(Integer.valueOf(element.getValue()));
                        } catch (NumberFormatException var10) {
                            jsonArray.element(Double.valueOf(element.getValue()));
                        }
                    } else if (type.compareToIgnoreCase("integer") == 0) {
                        jsonArray.element(Integer.valueOf(element.getValue()));
                    } else if (type.compareToIgnoreCase("float") == 0) {
                        jsonArray.element(Double.valueOf(element.getValue()));
                    } else if (type.compareToIgnoreCase("function") == 0) {
                        paramsAttribute = null;
                        params = element.getValue();
                        Attribute text = element.getAttribute(this.addJsonPrefix("params"));
                        if (text != null) {
                            paramsAttribute = StringUtils.split(text.getValue(), ",");
                        }

                        jsonArray.element(new JSONFunction(paramsAttribute, params));
                    } else if (type.compareToIgnoreCase("string") == 0) {
                        Attribute paramsAttribute1 = element.getAttribute(this.addJsonPrefix("params"));
                        if (paramsAttribute1 != null) {
                            params = null;
                            String text1 = element.getValue();
                            String[] params1 = StringUtils.split(paramsAttribute1.getValue(), ",");
                            jsonArray.element(new JSONFunction(params1, text1));
                        } else if (this.isArray(element, false)) {
                            jsonArray.element(this.processArrayElement(element, defaultType));
                        } else if (this.isObject(element, false)) {
                            jsonArray.element(this.simplifyValue((JSONObject) null, this.processObjectElement(element, defaultType)));
                        } else {
                            jsonArray.element(this.trimSpaceFromValue(element.getValue()));
                        }
                    }
                }

            }
        }
    }

    private void setValue(JSONObject jsonObject, Element element, String defaultType) {
        String clazz = this.getClass(element);
        String type = this.getType(element);
        type = type == null ? defaultType : type;
        String key = this.removeNamespacePrefix(element.getQualifiedName());
        if (this.hasNamespaces(element) && !this.skipNamespaces) {
            this.setOrAccumulate(jsonObject, key, this.simplifyValue(jsonObject, this.processElement(element, type)));
        } else {
            String[] params1;
            if (element.getAttributeCount() > 0 && this.isFunction(element)) {
                Attribute classProcessed1 = element.getAttribute(this.addJsonPrefix("params"));
                String paramsAttribute2 = element.getValue();
                params1 = StringUtils.split(classProcessed1.getValue(), ",");
                this.setOrAccumulate(jsonObject, key, new JSONFunction(params1, paramsAttribute2));
            } else {
                boolean classProcessed = false;
                if (clazz != null) {
                    if (clazz.compareToIgnoreCase("array") == 0) {
                        this.setOrAccumulate(jsonObject, key, this.processArrayElement(element, type));
                        classProcessed = true;
                    } else if (clazz.compareToIgnoreCase("object") == 0) {
                        this.setOrAccumulate(jsonObject, key, this.simplifyValue(jsonObject, this.processObjectElement(element, type)));
                        classProcessed = true;
                    }
                }

                if (!classProcessed) {
                    if (type.compareToIgnoreCase("boolean") == 0) {
                        this.setOrAccumulate(jsonObject, key, Boolean.valueOf(element.getValue()));
                    } else if (type.compareToIgnoreCase("number") == 0) {
                        try {
                            this.setOrAccumulate(jsonObject, key, Integer.valueOf(element.getValue()));
                        } catch (NumberFormatException var11) {
                            this.setOrAccumulate(jsonObject, key, Double.valueOf(element.getValue()));
                        }
                    } else if (type.compareToIgnoreCase("integer") == 0) {
                        this.setOrAccumulate(jsonObject, key, Integer.valueOf(element.getValue()));
                    } else if (type.compareToIgnoreCase("float") == 0) {
                        this.setOrAccumulate(jsonObject, key, Double.valueOf(element.getValue()));
                    } else {
                        String params;
                        if (type.compareToIgnoreCase("function") == 0) {
                            String[] paramsAttribute = null;
                            params = element.getValue();
                            Attribute text = element.getAttribute(this.addJsonPrefix("params"));
                            if (text != null) {
                                paramsAttribute = StringUtils.split(text.getValue(), ",");
                            }

                            this.setOrAccumulate(jsonObject, key, new JSONFunction(paramsAttribute, params));
                        } else if (type.compareToIgnoreCase("string") == 0) {
                            Attribute paramsAttribute1 = element.getAttribute(this.addJsonPrefix("params"));
                            if (paramsAttribute1 != null) {
                                params = null;
                                String text1 = element.getValue();
                                params1 = StringUtils.split(paramsAttribute1.getValue(), ",");
                                this.setOrAccumulate(jsonObject, key, new JSONFunction(params1, text1));
                            } else if (this.isArray(element, false)) {
                                this.setOrAccumulate(jsonObject, key, this.processArrayElement(element, defaultType));
                            } else if (this.isObject(element, false)) {
                                this.setOrAccumulate(jsonObject, key, this.simplifyValue(jsonObject, this.processObjectElement(element, defaultType)));
                            } else {
                                this.setOrAccumulate(jsonObject, key, this.trimSpaceFromValue(element.getValue()));
                            }
                        }
                    }
                }

            }
        }
    }

    private Object simplifyValue(JSONObject parent, Object json) {
        if (json instanceof JSONObject) {
            JSONObject object = (JSONObject) json;
            if (parent != null) {
                Iterator entries = parent.entrySet().iterator();

                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    if (key.startsWith("@xmlns") && value.equals(object.opt(key))) {
                        object.remove(key);
                    }
                }
            }

            if (object.size() == 1 && object.has("#text")) {
                return object.get("#text");
            }
        }

        return json;
    }

    private String trimSpaceFromValue(String value) {
        return this.isTrimSpaces() ? value.trim() : value;
    }

    private String writeDocument(Document doc, String encoding) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLSerializer.XomSerializer str;
        try {
            str = encoding == null ? new XMLSerializer.XomSerializer(baos) : new XMLSerializer.XomSerializer(baos, encoding);
            str.write(doc);
            encoding = str.getEncoding();
        } catch (IOException var7) {
            throw new JSONException(var7);
        }

        str = null;

        try {
            String str1 = baos.toString(encoding);
            return str1;
        } catch (UnsupportedEncodingException var6) {
            throw new JSONException(var6);
        }
    }


    private class XomSerializer extends Serializer {
        public XomSerializer(OutputStream out) {
            super(out);
        }

        public XomSerializer(OutputStream out, String encoding) throws UnsupportedEncodingException {
            super(out, encoding);
        }

        protected void write(Text text) throws IOException {
            String value = text.getValue();
            if (value.startsWith("<![CDATA[") && value.endsWith("]]>")) {
                value = value.substring(9);
                value = value.substring(0, value.length() - 3);
                this.writeRaw("<![CDATA[");
                this.writeRaw(value);
                this.writeRaw("]]>");
            } else {
                super.write(text);
            }

        }

        protected void writeEmptyElementTag(Element element) throws IOException {
            if (element instanceof XMLSerializer.CustomElement && XMLSerializer.this.isNamespaceLenient()) {
                this.writeTagBeginning((XMLSerializer.CustomElement) element);
                this.writeRaw("/>");
            } else {
                super.writeEmptyElementTag(element);
            }

        }

        protected void writeEndTag(Element element) throws IOException {
            if (element instanceof XMLSerializer.CustomElement && XMLSerializer.this.isNamespaceLenient()) {
                this.writeRaw("</");
                this.writeRaw(((XMLSerializer.CustomElement) element).getQName());
                this.writeRaw(">");
            } else {
                super.writeEndTag(element);
            }

        }

        protected void writeNamespaceDeclaration(String prefix, String uri) throws IOException {
            if (!StringUtils.isBlank(uri)) {
                super.writeNamespaceDeclaration(prefix, uri);
            }

        }

        protected void writeStartTag(Element element) throws IOException {
            if (element instanceof XMLSerializer.CustomElement && XMLSerializer.this.isNamespaceLenient()) {
                this.writeTagBeginning((XMLSerializer.CustomElement) element);
                this.writeRaw(">");
            } else {
                super.writeStartTag(element);
            }

        }

        private void writeTagBeginning(XMLSerializer.CustomElement element) throws IOException {
            this.writeRaw("<");
            this.writeRaw(element.getQName());
            this.writeAttributes(element);
            this.writeNamespaceDeclarations(element);
        }
    }

    private static class CustomElement extends Element {
        private String prefix;

        private static String getName(String name) {
            int colon = name.indexOf(58);
            return colon != -1 ? name.substring(colon + 1) : name;
        }

        private static String getPrefix(String name) {
            int colon = name.indexOf(58);
            return colon != -1 ? name.substring(0, colon) : "";
        }

        public CustomElement(String name) {
            super(getName(name));
            this.prefix = getPrefix(name);
        }

        public final String getQName() {
            return this.prefix.length() == 0 ? this.getLocalName() : this.prefix + ":" + this.getLocalName();
        }
    }
}
