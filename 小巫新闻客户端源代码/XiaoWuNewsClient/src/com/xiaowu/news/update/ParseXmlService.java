package com.xiaowu.news.update;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseXmlService {
	public HashMap<String, String> parseXml (InputStream inStream) throws Exception{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		//创建DocumentBuilderFactory，该对象将创建DocumentBuilder。
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//创建DocumentBuilder，DocumentBuilder将实际进行解析以创建Document对象
		DocumentBuilder builder = factory.newDocumentBuilder();
		//解析该文件以创建Document对象
		Document document = builder.parse(inStream);
		//获取XML文件根节点 
		Element root = document.getDocumentElement();
		//获得所有子节点
		NodeList childNodes = root.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = (Node) childNodes.item(i);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				//版本号 
				if("version".equals(childElement.getNodeName())) {
					hashMap.put("version", childElement.getFirstChild().getNodeValue());
				//软件名称 
				} else if("name".equals(childElement.getNodeName())) {
					hashMap.put("name", childElement.getFirstChild().getNodeValue());
				//下载地址
				} else if("url".equals(childElement.getNodeName())) {
					hashMap.put("url", childElement.getFirstChild().getNodeValue());
				}
			}
			
		}
		return hashMap;
	}
}
