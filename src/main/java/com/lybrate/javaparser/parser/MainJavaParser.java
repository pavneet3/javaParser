package com.lybrate.javaparser.parser;

import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;

import com.lybrate.javaparser.constants.Constant;
import com.lybrate.javaparser.helpers.DatabaseHelper;
import com.lybrate.javaparser.helpers.SheetHelper;
import com.lybrate.javaparser.utilities.ParserUtils;
import com.mongodb.client.FindIterable;

public class MainJavaParser {

	public static void main(String[] args) {

		SheetHelper oSheetHelper = new SheetHelper();
		ParserUtils oParserUtils = new ParserUtils();
		DatabaseHelper dbHelper = new DatabaseHelper();

		// read excel sheet and store data 
		List<LinkedHashMap<String, Object>> sheetData = oSheetHelper.readSheet();

		// create documents to be inserted from sheetData
		List<Document> documentsList = oParserUtils.getDocuments(sheetData);

		// perform db insertion of documents created using sheetData
		dbHelper.insertData(documentsList, Constant.DB_NAME, Constant.COLL_JAVA);

		// get data from db
		FindIterable<Document> documents = dbHelper.queryCollection(Constant.DB_NAME, Constant.COLL_JAVA);

		List<LinkedHashMap<String, Object>> writableData = oParserUtils.getWritableData(documents);

		//write data on excel sheet
		oSheetHelper.writeOnSheet(writableData);

	}

}
