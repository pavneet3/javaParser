package com.lybrate.javaparser.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;

import com.lybrate.javaparser.constants.Constant;
import com.lybrate.javaparser.constants.Constant.FieldNames;
import com.lybrate.javaparser.constants.Constant.SheetHeaders;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class ParserUtils {

	/**
	 * Returns documents to be stored in db using data read from sheet
	 * 
	 * @param sheetData
	 * @return List<LinkedHashMap<String, Object>>
	 */
	public List<Document> getDocuments(List<LinkedHashMap<String, Object>> sheetData) {

		List<Document> documentsList = new ArrayList<>();
		for (LinkedHashMap<String, Object> data : sheetData) {

			Document document = new Document();

			String dMode = (String) data.get(SheetHeaders.DELIVERY_MODE);
			String time = null;

			/* validation condition to be met for data to be parsed */
			if (dMode.contains(Constant.SCHD)) {
				String split[] = dMode.split("\\|");
				if (dMode.contains("|") && !"".equals(split[1]) && split[1].contains(":") && split[1].length() == 5) {
					time = getTimeValue(split[1]);
					dMode = split[0];
				} else {
					System.out.println(Constant.INVALID_PARSE_MSG + sheetData.indexOf(data));
					continue;
				}
			}

			Document webDoc = new Document(FieldNames.CHANNEL, Constant.WEB).append(FieldNames.TEXT,
					data.get(SheetHeaders.WEB_PN_DESCRIPTION));
			webDoc.append(FieldNames.TITLE, data.get(SheetHeaders.WEB_PN_TITLE));

			Document smsDoc = new Document(FieldNames.CHANNEL, Constant.SMS).append(FieldNames.TEXT,
					data.get(SheetHeaders.SMS_TEXT));

			List<Document> commDocs = new ArrayList<>();

			commDocs.add(smsDoc);
			commDocs.add(webDoc);

			document.append(FieldNames.DAY, data.get(SheetHeaders.DAYS));
			document.append(FieldNames.TYPE, data.get(SheetHeaders.TYPE));
			document.append(FieldNames.MODE, data.get(SheetHeaders.MODE));
			document.append(FieldNames.D_MODE, dMode);
			document.append(FieldNames.TIME, time);
			document.append(FieldNames.REF_CODE, data.get(SheetHeaders.REF_CODE));
			document.append(FieldNames.TEXT, data.get(SheetHeaders.TEXT));
			document.append(FieldNames.TITLE, data.get(SheetHeaders.TITLE));
			document.append(FieldNames.COMM, commDocs);

			documentsList.add(document);
		}

		return documentsList;

	}

	/**
	 * Returns String of time value to be stored in db
	 * 
	 * @param time
	 * @return String
	 */
	private static String getTimeValue(String time) {

		String[] values = time.split(":");

		Integer hour = Integer.parseInt(values[0]);
		Integer min = Integer.parseInt(values[1]);

		return String.valueOf((hour * 60) + min);

	}

	/**
	 * Returns data to be written on sheet
	 * 
	 * @param documents
	 * @return List<LinkedHashMap<String, Object>>
	 */
	public List<LinkedHashMap<String, Object>> getWritableData(FindIterable<Document> documents) {

		List<LinkedHashMap<String, Object>> sheetData = new ArrayList<>();

		documents.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {

				LinkedHashMap<String, Object> data = new LinkedHashMap<>();

				String dMode = doc.get(FieldNames.D_MODE).toString();

				/* creation of delivery_mode cell data */
				if (dMode.contains(Constant.SCHD)) {
					Integer time = Integer.parseInt(doc.get(FieldNames.TIME).toString());
					dMode = dMode + "|" + getTimeString(time);
				}

				List<Document> comm = (List<Document>) doc.get(FieldNames.COMM);

				Document smsDoc = null;
				Document webDoc = null;

				for (Document document : comm) {
					switch (document.get(FieldNames.CHANNEL).toString()) {
					case Constant.SMS:
						smsDoc = document;
						break;
					case Constant.WEB:
						webDoc = document;
						break;
					default:
						break;
					}
				}

				data.put(SheetHeaders.DAYS, doc.get(FieldNames.DAY));
				data.put(SheetHeaders.TITLE, doc.get(FieldNames.TITLE));
				data.put(SheetHeaders.TYPE, doc.get(FieldNames.TYPE));
				data.put(SheetHeaders.TEXT, doc.get(FieldNames.TEXT));
				data.put(SheetHeaders.DELIVERY_MODE, dMode);
				data.put(SheetHeaders.MODE, doc.get(FieldNames.MODE));
				data.put(SheetHeaders.REF_CODE, doc.get(FieldNames.REF_CODE));

				if (smsDoc != null) {
					data.put(SheetHeaders.SMS_TEXT, smsDoc.get(FieldNames.TEXT));
				}
				if (webDoc != null) {
					data.put(SheetHeaders.WEB_PN_TITLE, webDoc.get(FieldNames.TITLE));
					data.put(SheetHeaders.WEB_PN_DESCRIPTION, webDoc.get(FieldNames.TEXT));

				}
				sheetData.add(data);

			}
		});

		return sheetData;

	}

	/**
	 * Returns value of time to be written on sheet
	 * 
	 * @param time
	 * @return String
	 */
	private String getTimeString(Integer time) {
		int hour = time / 60;
		int min = time % 60;
		return hour + ":" + min;
	}

}
