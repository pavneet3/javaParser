package com.lybrate.javaparser.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import com.lybrate.javaparser.constants.Constant;

public class SheetHelper {

	/**
	 * Reads data from excel sheet at index 0
	 * 
	 * @return List<LinkedHashMap<String, Object>>
	 */
	public List<LinkedHashMap<String, Object>> readSheet() {
		FileInputStream file;
		HSSFWorkbook workbook;
		List<LinkedHashMap<String, Object>> sheetData = new ArrayList<>();

		try {
			file = new FileInputStream(new File(Constant.SHEET_TO_BE_PARSED));
			workbook = new HSSFWorkbook(file);

			HSSFSheet sheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			HashMap<Integer, String> headerData = new HashMap<>();

			for (int j = 0; j < sheet.getLastRowNum(); j++) {

				Row row = sheet.getRow(j);

				LinkedHashMap<String, Object> rowData = new LinkedHashMap<>();

				/* skip when first column is empty considering it as primary value */
				if ("".equals(dataFormatter.formatCellValue(row.getCell(0)))) {
					continue;
				}

				for (int i = 0; i < row.getLastCellNum(); i++) {
					String cellValue = dataFormatter.formatCellValue(row.getCell(i));

					if (row.getRowNum() == 0) {
						headerData.put(i, cellValue);
					} else {
						rowData.put(headerData.get(i), cellValue);
					}
				}

				if (row.getRowNum() != 0) {
					sheetData.add(rowData);
				}
			}

			file.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sheetData;
	}

	/**
	 * Writes data from excel sheet at index 0
	 * 
	 * @return boolean
	 */
	public void writeOnSheet(List<LinkedHashMap<String, Object>> sheetData) {

		FileInputStream file;
		HSSFWorkbook workbook;

		try {
			file = new FileInputStream(new File(Constant.SHEET_TO_BE_POPULATED));

			workbook = new HSSFWorkbook(file);

			HSSFSheet sheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			HashMap<Integer, String> headerData = new HashMap<>();

			Row row = sheet.getRow(0);

			for (int i = 0; i < row.getLastCellNum(); i++) {
				String cellValue = dataFormatter.formatCellValue(row.getCell(i));

				if (row.getRowNum() == 0) {
					headerData.put(i, cellValue);
				}
			}

			file.close();

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				clearRow(sheet.getRow(i));
			}

			for (int i = 0; i < sheetData.size(); i++) {

				row = sheet.getRow(i + 1);

				if (row == null) {
					row = sheet.createRow(i + 1);
				}

				for (int j = 0; j < sheetData.get(i).size(); j++) {

					Cell cell = row.getCell(j);

					if (cell == null) {
						cell = row.createCell(j);
					}
					cell.setCellStyle(getCellStyle(workbook));

					LinkedHashMap<String, Object> rowData = sheetData.get(i);

					String columnData = (String) rowData.get(headerData.get(j));

					cell.setCellValue(columnData);
				}

			}

			FileOutputStream outputStream = new FileOutputStream(new File(Constant.SHEET_TO_BE_POPULATED));
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clearRow(Row row) {
		for (int j = 0; j < row.getLastCellNum(); j++) {
			Cell cell = row.getCell(j);
			if (cell != null) {
				cell.setCellValue("");
			}
		}
	}

	/**
	 * Utility method to provide style to cells in sheet
	 * 
	 * @return List<LinkedHashMap<String, Object>>
	 */
	private HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setRightBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		return style;
	}

}
