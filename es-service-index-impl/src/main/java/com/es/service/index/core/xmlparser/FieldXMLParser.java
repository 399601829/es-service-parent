package com.es.service.index.core.xmlparser;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.type.IndexType;
import com.es.service.index.common.conf.ClassPathResourcesReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * 解析Field-xml文件
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月15日
 * 
 */
public class FieldXMLParser {

    private static final Logger log = LoggerFactory.getLogger(FieldXMLParser.class);

    /**
     * 读取配置的默认目录
     * 
     */
    private String fieldXmlDir = "field-conf";

    /**
     * filed元数据缓存
     */
    private static volatile Map<Integer, Fileds> indexMetedata;

    /**
     * 不存在则返回 + 缓存数据
     * 
     * @return
     */
    public static Map<Integer, Fileds> getAndCache() {
        if (indexMetedata == null) {
            synchronized (FieldXMLParser.class) {
                if (indexMetedata == null) {
                    indexMetedata = indexMetedata == null ? new FieldXMLParser().parserField()
                            : indexMetedata;
                }
            }
        }
        return indexMetedata;
    }

    /**
     * 读取并解析Field-xml文件
     * 
     */
    public Map<Integer, Fileds> parserField() {
        List<String> resources = ClassPathResourcesReader
                .getResourcePath(fieldXmlDir, null, ".xml");
        Fileds fls;
        Map<Integer, Fileds> fields = Maps.newHashMap();
        for (String resource : resources) {
            fls = parserXml(ClassPathResourcesReader.getResource(resource));
            int indexNo = IndexType.getIndexTypeByIndexNameAndType(fls.getIndexName(),
                    fls.getTypeName()).getIndexNo();
            fields.put(indexNo, fls);
        }
        log.info("parser Field XML numbers:{} the XML is:{}", resources.size(), resources);
        return fields;
    }

    /**
     * 解析输入流为Fileds
     * 
     * @param in
     * @return
     * @throws DocumentException
     */
    private Fileds parserXml(InputStream in) {
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(in);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element elements = document.getRootElement();
        List<Filed> filedList = Lists.newArrayList();
        Fileds fileds = new Fileds();
        Filed filed;
        String indexname = "";
        String typename = "";
        for (Iterator i = elements.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            filed = new Filed();
            // 解析Field
            if (element.getName().equals("index")) {
                indexname = element.attributeValue("indexname");
                typename = element.attributeValue("typename");
            }
            if (element.getName().equals("uniqueKey")) {
                fileds.setKey(element.attributeValue("key"));
            }
            if (element.getName().equals("suggest")) {
                fileds.setIssuggest(true);
            }
            if (element.getName().equals("field")) {
                filed.setName(element.attributeValue("name"));
                filed.setType(element.attributeValue("type"));
                filed.setIsindex(element.attributeValue("indexed") == null ? true : element
                        .attributeValue("indexed").equals("true"));
                filed.setIsstore(element.attributeValue("stored") == null ? true : element
                        .attributeValue("stored").equals("true"));
                filed.setPosition_offset_gap(element.attributeValue("position_offset_gap") == null ? 0
                        : Integer.parseInt(element.attributeValue("position_offset_gap")));
                if (null != element.attributeValue("copyto")) {
                    filed.setIscopy(true);
                    filed.setCopyto(element.attributeValue("copyto"));
                } else {
                    filed.setIscopy(false);
                }
                filed.setIsdefaultsearch(element.attributeValue("isdefaultsearch") == null ? false
                        : element.attributeValue("isdefaultsearch").equals("true"));
                filed.setAnalyzer(element.attributeValue("isAnalyzer") == null ? false : element
                        .attributeValue("isAnalyzer").equals("true"));
                // 确定分词
                if (filed.isAnalyzer()) {
                    filed.setAnalyzer(element.attributeValue("analyzer") == null ? "ik_max_word"
                            : element.attributeValue("analyzer"));
                    filed.setIndexAnalyzer(element.attributeValue("indexAnalyzer") == null ? true
                            : element.attributeValue("indexAnalyzer").equals("true"));
                }
                // 权重
                String weight = element.attributeValue("weight");
                if (StringUtils.isNotBlank(weight)) {
                    filed.setWeight(Double.parseDouble(weight));
                }
                // date格式化
                String format = element.attributeValue("format");
                if (StringUtils.isNotBlank(format)) {
                    filed.setFormat(format);
                }
                filedList.add(filed);
            }
            // List<Filed> _filedList = new ArrayList<Filed>();
            // if (element.getName().equals("copyField")) {
            // Filed f3 = new Filed();
            // f3.setName(element.attributeValue("source"));
            // f3.setType(element.attributeValue("type"));
            // for (Filed fld : filedList) {
            // if (fld.getName().equalsIgnoreCase(element.attributeValue("dest"))
            // && fld.isIscopy()) {
            // _filedList = fld.getNames() == null ? _filedList : fld.getNames();
            // _filedList.add(f3);
            // fld.setNames(_filedList);
            // }
            // }
            // }
        }

        fileds.setListfiled(filedList);
        fileds.setIndexName(indexname);
        fileds.setTypeName(typename);
        return fileds;
    }
}
