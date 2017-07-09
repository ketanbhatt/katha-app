package com.ketanbhatt.kathaapp.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Simar Arora on 09/07/17.
 */

public class TocParser {

    private static String tocSample = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
            "<ncx version=\"2005-1\" xmlns=\"http://www.daisy.org/z3986/2005/ncx/\">\n" +
            "<head>\n" +
            "<meta name=\"dtb:uid\" content=\"isbn:978-93-82454-23-6\"/>\n" +
            "<meta name=\"dtb:depth\" content=\"1\"/>\n" +
            "<meta name=\"dtb:totalPageCount\" content=\"30\"/>\n" +
            "<meta name=\"dtb:maxPageNumber\" content=\"0\"/>\n" +
            "</head>\n" +
            "<docTitle>\n" +
            "<text>Hulgul ka Pitara: Gilli Gilli Gola</text>\n" +
            "</docTitle>\n" +
            "<navMap>\n" +
            "<navPoint id=\"pg01\" playOrder=\"1\"><navLabel><text>Frontcover</text></navLabel><content src=\"01_frontcover.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg02\" playOrder=\"2\"><navLabel><text>Page 2</text></navLabel><content src=\"02_pg02.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg03\" playOrder=\"3\"><navLabel><text>Page 3</text></navLabel><content src=\"03_pg03.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg04\" playOrder=\"4\"><navLabel><text>Page 4</text></navLabel><content src=\"04_pg04.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg05\" playOrder=\"5\"><navLabel><text>Page 5</text></navLabel><content src=\"05_pg05.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg06\" playOrder=\"6\"><navLabel><text>Page 6</text></navLabel><content src=\"06_pg06.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg07\" playOrder=\"7\"><navLabel><text>Page 7</text></navLabel><content src=\"07_pg07.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg08\" playOrder=\"8\"><navLabel><text>Page 8</text></navLabel><content src=\"08_pg08.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg09\" playOrder=\"9\"><navLabel><text>Page 9</text></navLabel><content src=\"09_pg09.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg10\" playOrder=\"10\"><navLabel><text>Page 10</text></navLabel><content src=\"10_pg10.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg11\" playOrder=\"11\"><navLabel><text>Page 11</text></navLabel><content src=\"11_pg11.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg12\" playOrder=\"12\"><navLabel><text>Page 12</text></navLabel><content src=\"12_pg12.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg13\" playOrder=\"13\"><navLabel><text>Page 13</text></navLabel><content src=\"13_pg13.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg14\" playOrder=\"14\"><navLabel><text>Page 14</text></navLabel><content src=\"14_pg14.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg15\" playOrder=\"15\"><navLabel><text>Page 15</text></navLabel><content src=\"15_pg15.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg16\" playOrder=\"16\"><navLabel><text>Page 16</text></navLabel><content src=\"16_pg16.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg17\" playOrder=\"17\"><navLabel><text>Page 17</text></navLabel><content src=\"17_pg17.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg18\" playOrder=\"18\"><navLabel><text>Page 18</text></navLabel><content src=\"18_pg18.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg19\" playOrder=\"19\"><navLabel><text>Page 19</text></navLabel><content src=\"19_pg19_Act1.xhtml\"/></navPoint>\n" +
            "<navPoint id=\"pg20\" playOrder=\"20\"><navLabel><text>Page 30</text></navLabel><content src=\"20_pg20.xhtml\"/></navPoint>\n" +
            "</navMap>\n" +
            "</ncx>\n";

    public static List<TOCItem> processXML(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder;
        Document xmlDocument = null;
        try {
            documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();
            xmlDocument = documentBuilder.parse(new ByteArrayInputStream(
                    tocSample.getBytes(Charset.forName("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (xmlDocument != null) {
                List<TOCItem> tocItems = new ArrayList<>();
                NodeList navElements = xmlDocument.getElementsByTagName("navPoint");
                for (int i = 0; i < navElements.getLength(); i++) {
                    Node navNode = navElements.item(i);
                    String id = navNode.getAttributes().getNamedItem("id").getTextContent();
                    String playOrder = navNode.getAttributes().getNamedItem("playOrder").getTextContent();
                    String source = null;
                    String title = null;
                    NodeList childNodeList = navNode.getChildNodes();
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        Node childNode = childNodeList.item(j);
                        if ("navLabel".equals(childNode.getNodeName())) {
                            title = childNode.getFirstChild().getTextContent();
                        } else if ("content".equals(childNode.getNodeName())) {
                            source = childNode.getAttributes().getNamedItem("src").getTextContent();
                        }
                    }
                    tocItems.add(new TOCItem(id, playOrder, title, source));
                }
                return tocItems;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class TOCItem {
        String id;
        String playOrder;
        String title;
        String source;

        TOCItem(String id, String playOrder, String title, String source) {
            this.id = id;
            this.playOrder = playOrder;
            this.title = title;
            this.source = source;
        }
    }
}
